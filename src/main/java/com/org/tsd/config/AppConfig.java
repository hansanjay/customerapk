package com.org.tsd.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ilsc")
public class AppConfig {

    private String addInId;
    private Path stagingDir;
    private boolean devMode = false;

    public String getAddInId() {
        return addInId;
    }

    public void setAddInId(String addInId) {
        this.addInId = addInId;
    }

    public Path getStagingDir() {
        return stagingDir;
    }

    public void setStagingDir(Path stagingDir) {
        this.stagingDir = stagingDir;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

//    @PostConstruct
//    public void init() {
//        try {
//            Resource resource = new ClassPathResource("static/data/catalog.json");
//            listings = mapper.readValue(resource.getInputStream(), new TypeReference<List<ProductListing>>() {});
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load catalog.json", e);
//        }
//    }
//
//    public List<ProductListing> getListings() {
//        return listings;
//    }
}
