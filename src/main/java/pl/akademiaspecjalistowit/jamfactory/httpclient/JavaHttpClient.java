package pl.akademiaspecjalistowit.jamfactory.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public class JavaHttpClient {

    public HttpResponse<String> httpPost(String body, String uri)
        throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(uri))
            .version(HttpClient.Version.HTTP_2)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        return HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
    }
}
