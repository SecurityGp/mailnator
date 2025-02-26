package com.assured.tests;

import com.assured.common.ApiHelper;
import com.assured.common.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class T03_ProviderOnboarding extends BaseTest {

    private ApiHelper apiHelper = new ApiHelper();

    @Test
    public void testProviderInvite() {
        // Use the inherited Page instance from BaseTest
        page.navigate("http://your-app-url.com/provider-invite");
        // Your provider invite test code here...
    }

    /**
     * This cleanup method is executed after all tests in this class,
     * and it calls the ApiHelper cleanup method to delete account data.
     */
    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        // Call the cleanup method from the ApiHelper instance.
        apiHelper.cleanUpAccountData();
    }
}

