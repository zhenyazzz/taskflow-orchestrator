package org.example.gatewayservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (config.isLogRequest()) {
                logger.info("Incoming Request: {} {} from {}",
                        exchange.getRequest().getMethod(),
                        exchange.getRequest().getPath(),
                        exchange.getRequest().getRemoteAddress());
                if (config.isShowHeaders()) {
                    logger.info("Request Headers: {}", exchange.getRequest().getHeaders());
                }
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isLogResponse()) {
                    logger.info("Outgoing Response: Status {} for {} {} to {}",
                            exchange.getResponse().getStatusCode(),
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath(),
                            exchange.getRequest().getRemoteAddress());
                    if (config.isShowResponseHeaders()) {
                        logger.info("Response Headers: {}", exchange.getResponse().getHeaders());
                    }
                }
            }));
        };
    }

    public static class Config {
        private boolean logRequest = true;
        private boolean logResponse = true;
        private boolean showHeaders = true;
        private boolean showBody = false;
        private boolean showRequestHeaders = true;
        private boolean showResponseHeaders = true;
        private String level = "INFO";

        public boolean isLogRequest() {
            return logRequest;
        }

        public void setLogRequest(boolean logRequest) {
            this.logRequest = logRequest;
        }

        public boolean isLogResponse() {
            return logResponse;
        }

        public void setLogResponse(boolean logResponse) {
            this.logResponse = logResponse;
        }

        public boolean isShowHeaders() {
            return showHeaders;
        }

        public void setShowHeaders(boolean showHeaders) {
            this.showHeaders = showHeaders;
        }

        public boolean isShowBody() {
            return showBody;
        }

        public void setShowBody(boolean showBody) {
            this.showBody = showBody;
        }

        public boolean isShowRequestHeaders() {
            return showRequestHeaders;
        }

        public void setShowRequestHeaders(boolean showRequestHeaders) {
            this.showRequestHeaders = showRequestHeaders;
        }

        public boolean isShowResponseHeaders() {
            return showResponseHeaders;
        }

        public void setShowResponseHeaders(boolean showResponseHeaders) {
            this.showResponseHeaders = showResponseHeaders;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }
    }
}
