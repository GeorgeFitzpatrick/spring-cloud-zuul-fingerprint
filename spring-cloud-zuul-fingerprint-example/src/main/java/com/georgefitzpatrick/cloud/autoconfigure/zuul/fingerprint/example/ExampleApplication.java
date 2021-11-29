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

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.example;

import com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.config.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ApplicationContext;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@EnableZuulProxy
@SpringBootApplication
public class ExampleApplication {

    /* ----- Fields ----- */

    private final ApplicationContext context;
    private final RedisServer redisServer;

    /* ----- Constructors ----- */

    @Autowired
    public ExampleApplication(ApplicationContext context, @Value("${spring.redis.port}") int port) {
        this.context = context;
        this.redisServer = new RedisServer(port);
    }

    /* ----- Methods ----- */

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @PostConstruct
    public void startRedis() {
        redisServer.start();
    }

    @PostConstruct
    public void initDataset() {
        Map<String, String> data = new HashMap<>();
        data.put("709F25ED711658651499E728CB08207F", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:94.0) Gecko/20100101 Firefox/94.0");
        data.put("414E67FE88AADB19E20AFD3CCA7BD1F5", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:94.0) Gecko/20100101 Firefox/94.0");
        data.put("E49F38F4B06FB10A79BDF41C1BD524BF", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15");
        data.put("846C1BE47E8239C2517CC220CB826AFB", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15");
        data.put("0EC49D970C80CD6909EF8D6EF38C60EA", "PostmanRuntime/7.28.4");
        data.put("1B160A0280C24024045FB4311CF1C4B8", "curl/7.64.1");
        data.put("C6C7FBED2D22FEC5267343673D3BA4AE", "curl/7.64.1");

        Dataset dataset = context.getBean(Dataset.class);
        dataset.setAll(data);
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }

}
