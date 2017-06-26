package ru.sbt.qa.tde.loaders;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.sbt.qa.tde.core.ILoader;
import ru.sbt.qa.tde.core.RudeEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

/**
 * Created by cyberspace on 6/25/2017.
 */
public class XLSXLoader implements ILoader {

    private final String pathname;
    private final DataFormatter dataFormatter = new DataFormatter();

    public XLSXLoader(String pathname) {
        this.pathname = pathname;
    }

    public List<RudeEntity> load() {
        return ofNullable(getWorkbook(pathname))
                .map(x -> {
                    List<RudeEntity> result = new ArrayList<>();
                    for (int i = 0; i < x.getNumberOfSheets(); i++) {
                        result.addAll(ofNullable(getRowEntitiesBySheet(x.getSheetAt(i)))
                                .orElse(new ArrayList<>()));
                    }
                    return result;
                })
                .orElse(null);
    }

    private List<RudeEntity> getRowEntitiesBySheet(XSSFSheet sheet) {
        Stream<Row> rowStream = ofNullable(sheet)
                .map(XSSFSheet::iterator)
                .map(x -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(x, Spliterator.ORDERED),false))
                .orElse(null);
        return ofNullable(rowStream)
                .map(x -> {

                    final List<RudeEntity> result = new ArrayList<>();
                    x.forEach(row -> {
                        final String firstCellValue = getCellValue(row.getCell(0));
                        if (ofNullable(firstCellValue).filter(v -> v.contains("*")).map(v -> true).orElse(false)) {
                            // Если эта строка с объявление Сущности
                            final Cell entityCell = row.getCell(0);
                            final Cell entityInstanceNameCell = row.getCell(1);
                            // Создаем новую сущность
                            result.add(new RudeEntity(getCellValue(entityCell).replace("*", ""), getCellValue(entityInstanceNameCell)));
                        }
                        else {
                            // Если эта строка с объявлением Поля Сущности
                            final Cell fieldNameCell = row.getCell(1);
                            final Cell fieldValueCell = row.getCell(2);
                            // Добавляем к последней сущности
                            of(result).filter(r -> r.size() > 0)
                                    .map(r -> result.get(result.size() - 1))
                                    .map(entity -> entity.addField(getCellValue(fieldNameCell), getCellValue(fieldValueCell)));
                        }
                    });
                    return result;

                }).orElse(null);
    }

    private String getCellValue(Cell cell) {
        return  ofNullable(cell)
                .map(dataFormatter::formatCellValue)
                .orElse(null);
    }

    private XSSFWorkbook getWorkbook(String pathname) {
        File file = new File(pathname);
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public String getPathname() {
        return pathname;
    }

}
