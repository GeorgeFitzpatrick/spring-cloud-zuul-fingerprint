/*
 * Copyright 2021 George Fitzpatrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint;

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.*;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.data.redis.RedisBucket;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.data.redis.RedisDataset;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.properties.FingerprintProperties;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.filter.TrustFilter;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.http2.HTTP2CConnectionFactory;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.http2.HTTP2ConnectionFactory;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.http2.HTTP2Fingerprinter;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.CustomALPNServerConnectionFactory;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.CustomSSLContextFactory;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.ssl.JA3Fingerprinter;
import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util.Matcher;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * @author George Fitzpatrick
 */
@Configuration
@EnableConfigurationProperties(FingerprintProperties.class)
@ConditionalOnProperty(prefix = FingerprintProperties.PREFIX, name = "enabled", havingValue = "true")
public class FingerprintAutoConfiguration {

    /* ----- Fields ----- */

    private final ApplicationContext context;

    /* ----- Constructors ----- */

    @Autowired
    public FingerprintAutoConfiguration(ApplicationContext context) {
        this.context = context;
    }

    /* ----- Methods ----- */

    @PostConstruct
    private void customizeJetty() {
        JettyServerCustomizer customizer = context.getBean(JettyServerCustomizer.class);
        JettyServletWebServerFactory factory = context.getBean(JettyServletWebServerFactory.class);
        factory.addServerCustomizers(customizer);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeyGenerator keyGenerator() {
        return (request, route, origin) -> String.format("%s:%s", route.getId(), origin);
    }

    @Bean
    @ConditionalOnMissingBean
    public Matcher<String> matcher() {
        return String::equals;
    }

    @Bean
    public TrustFilter trustFilter(FingerprintProperties properties, Bucket bucket, Dataset dataset,
                                   List<Fingerprinter> fingerprinters, RouteLocator routeLocator,
                                   KeyGenerator keyGenerator, Matcher<String> matcher) {
        return new TrustFilter(properties, fingerprinters, matcher, routeLocator, keyGenerator, bucket, dataset);
    }

    /* ----- Classes ----- */

    @Configuration
    @ConditionalOnClass(StringRedisTemplate.class)
    public static class RedisConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Bucket redisBucket(StringRedisTemplate redis) {
            return new RedisBucket(redis);
        }

        @Bean
        @ConditionalOnMissingBean
        public Dataset redisDataset(StringRedisTemplate redis) {
            return new RedisDataset(redis);
        }

    }

    @Configuration
    public static class FingerprinterConfiguration {

        @Bean
        @ConditionalOnExpression("${server.ssl.enabled}")
        public Fingerprinter ja3Fingerprinter() {
            return new JA3Fingerprinter();
        }

        @Bean
        @ConditionalOnExpression("${server.http2.enabled}")
        public Fingerprinter http2Fingerprinter() {
            return new HTTP2Fingerprinter();
        }

    }

    @Configuration
    @ConditionalOnExpression("!${server.ssl.enabled}")
    //@ConditionalOnBean(JettyServletWebServerFactory.class)
    public static class JettyServerConfiguration {

        @Bean
        @ConditionalOnExpression("!${server.http2.enabled}")
        public JettyServerCustomizer serverCustomizer(@Value("${server.port}") int port) {
            return server -> {
                HttpConnectionFactory http = new HttpConnectionFactory();

                ConnectionFactory[] connections = {http};
                ServerConnector connector = new ServerConnector(server, connections);
                connector.setPort(port);

                Connector[] connectors = {connector};
                server.setConnectors(connectors);
            };
        }

        @Bean
        @ConditionalOnExpression("${server.http2.enabled}")
        public JettyServerCustomizer http2ServerCustomizer(@Value("${server.port}") int port) {
            return server -> {
                HttpConfiguration httpConfig = new HttpConfiguration();
                HttpConnectionFactory http = new HttpConnectionFactory();

                HTTP2CServerConnectionFactory http2c = new HTTP2CConnectionFactory(httpConfig);

                ConnectionFactory[] connections = {http2c, http};
                ServerConnector connector = new ServerConnector(server, connections);
                connector.setPort(port);

                Connector[] connectors = {connector};
                server.setConnectors(connectors);
            };
        }

    }

    @Configuration
    @ConditionalOnExpression("${server.ssl.enabled}")
    //@ConditionalOnBean(JettyServletWebServerFactory.class)
    public static class JettySecureServerConfiguration {

        @Bean
        public SslContextFactory sslContextFactory(@Value("${server.ssl.enabled-protocols}") String[] protocols,
                                                   @Value("${server.ssl.key-store-type}") String keyStoreType,
                                                   @Value("${server.ssl.key-store}") @NotNull Resource keyStore,
                                                   @Value("${server.ssl.key-store-password}") String keyStorePassword,
                                                   @Value("${server.ssl.key-alias}") String keyAlias) throws IOException {
            SslContextFactory.Server factory = new CustomSSLContextFactory();
            factory.setIncludeProtocols(protocols);
            factory.setKeyStoreType(keyStoreType);
            factory.setKeyStorePath(keyStore.getURL().toExternalForm());
            factory.setKeyStorePassword(keyStorePassword);
            factory.setCertAlias(keyAlias);
            return factory;
        }

        @Bean
        public HttpConfiguration httpConfiguration(@Value("${server.port}") int port) {
            HttpConfiguration configuration = new HttpConfiguration();
            configuration.setSecureScheme("https");
            configuration.setSecurePort(port);
            configuration.addCustomizer(new SecureRequestCustomizer());
            return configuration;
        }

        @Bean
        public HttpConnectionFactory httpConnectionFactory(HttpConfiguration configuration) {
            return new HttpConnectionFactory(configuration);
        }

        @Bean
        @ConditionalOnExpression("!${server.http2.enabled}")
        public JettyServerCustomizer serverCustomizer(@Value("${server.port}") int port,
                                                      SslContextFactory sslContextFactory, HttpConnectionFactory http) {
            return server -> {
                SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, http.getProtocol());

                ConnectionFactory[] connections = {ssl, http};
                ServerConnector connector = new ServerConnector(server, connections);
                connector.setPort(port);

                Connector[] connectors = {connector};
                server.setConnectors(connectors);
            };
        }

        @Bean
        @ConditionalOnExpression("${server.http2.enabled}")
        public JettyServerCustomizer http2ServerCustomizer(@Value("${server.port}") int port,
                                                           SslContextFactory sslContextFactory,
                                                           HttpConfiguration httpConfig, HttpConnectionFactory http) {
            return server -> {
                HTTP2ServerConnectionFactory http2 = new HTTP2ConnectionFactory(httpConfig);

                ALPNServerConnectionFactory alpn = new CustomALPNServerConnectionFactory(
                        http2.getProtocol(),
                        http.getProtocol());
                alpn.setDefaultProtocol(http.getProtocol());

                SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

                ConnectionFactory[] connections = {ssl, alpn, http2, http};
                ServerConnector connector = new ServerConnector(server, connections);
                connector.setPort(port);

                Connector[] connectors = {connector};
                server.setConnectors(connectors);
            };
        }

    }

}
