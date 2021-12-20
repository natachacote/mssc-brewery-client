package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jt on 2019-08-08.
 */
@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {

	@Autowired
	private Environment env;

	private final Integer maxTotalConnetions;
	private final Integer defaultMaxPerRoute;
	private final Integer requestTimeout;
	private final Integer socketTimeout;

	public BlockingRestTemplateCustomizer(@Value("${connectionManager.maxConnexion}") Integer maxTotalConnetions,
			@Value("${connectionManager.defaultMaxPerRoute}") Integer defaultMaxPerRoute,
			@Value("${restConfig.requestTimeout}") Integer requestTimeout,
			@Value("${restConfig.socketTimeout}") Integer socketTimeout) {
		super();
		this.maxTotalConnetions = maxTotalConnetions;
		this.defaultMaxPerRoute = defaultMaxPerRoute;
		this.requestTimeout = requestTimeout;
		this.socketTimeout = socketTimeout;
	}

	public ClientHttpRequestFactory clientHttpRequestFactory() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

		connectionManager.setMaxTotal(maxTotalConnetions);
		connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(requestTimeout)
				.setSocketTimeout(socketTimeout).build();

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
				.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()).setDefaultRequestConfig(requestConfig)
				.build();

		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	@Override
	public void customize(RestTemplate restTemplate) {
		restTemplate.setRequestFactory(this.clientHttpRequestFactory());
	}
}
