package com.github.tcurrie.rest.factory.it;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DeployIT extends RestServiceTestBasis {

    @Test
    public void shouldDeployServer() {
        getWebdriver().get(RestServers.SERVER.getUrl());
        final WebElement body = getWebdriver().findElement(By.xpath("//BODY"));
        assertThat(body.getText(), is("Hello World"));
    }

}
