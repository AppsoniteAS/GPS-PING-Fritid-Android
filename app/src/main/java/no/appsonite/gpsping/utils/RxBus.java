package no.appsonite.gpsping.utils;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 28.01.2016
 */
public class RxBus {
    private static final RxBus INSTANCE = new RxBus();
    private final Subject<Object, Object> busSubject = new SerializedSubject<>(PublishSubject.create());

    public static RxBus getInstance() {
        return INSTANCE;
    }

    public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
        return busSubject.filter(event -> event.getClass().equals(eventClass))
                .map(o -> (T) o)
                .subscribe(onNext);
    }

    public void post(Object event) {
        busSubject.onNext(event);
    }
}
