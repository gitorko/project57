package com.demo.project57.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RefreshScope
@Data
@Slf4j
public class CloudConfig {
    @Value("${project57.newFeatureFlag:false}")
    private Boolean newFeatureFlag;

    @SuppressWarnings("unused")
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh(RefreshScopeRefreshedEvent event) {
        log.info("property refreshed!");
    }
}
