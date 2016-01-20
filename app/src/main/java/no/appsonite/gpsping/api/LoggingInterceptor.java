package no.appsonite.gpsping.api;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by gorodechnyj on 10.09.15.
 */
public class LoggingInterceptor implements Interceptor {
    private static Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        log.debug(String.format("Request: %s on %s [headers: %s]",
                request.url(), chain.connection(), formatHeaders(request.headers())));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        log.debug(String.format("Response for %s in %.1fms [headers: %s]",
                response.request().url(), (t2 - t1) / 1e6d, formatHeaders(response.headers())));

        return response;
    }

    private String formatHeaders(Headers headers) {
        if (headers == null) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0, size = headers.size(); i < size; i++) {
            result.append(headers.name(i)).append(": ")
                    .append(headers.value(i));
            if (i < headers.size() - 1) {
                result.append(";;");
            }
        }
        return result.toString();
    }
}
