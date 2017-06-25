package ru.sbt.qa.tde.core;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

import java.util.function.Consumer;

/**
 * Created by cyberspace on 6/23/2017.
 */
public class MManager {

    public static <T, R> BlockingObservable<? extends R> execute(Observable<T> observable, final Func1<? super T, ? extends R> func) {
        /* Example:
        ru.sbt.qa.tde.core.MManager.execute(Observable.range(0, 10), x -> x * 2)
                .subscribe(System.out::println);
        */
        return observable.flatMap(object ->
                Observable.just(object)
                        .map(func)
                        .subscribeOn(Schedulers.computation())
        ).toBlocking();
    }

    public static <T> BlockingObservable<T> executeActions(Observable<T> observable, final Consumer<T> consumer) {
        return observable.flatMap(object ->
                Observable.just(object)
                        .map(x -> {
                            consumer.accept(x);
                            return x;
                        })
                        .subscribeOn(Schedulers.computation())
        ).toBlocking();
    }
}
