package com.groupfive.ewastemanagement.helper;

import com.groupfive.ewastemanagement.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static com.groupfive.ewastemanagement.helper.Constants.API_HAS_STARTED_SUCCESSFULLY;
import static com.groupfive.ewastemanagement.helper.Constants.EMAIL_CANNOT_BE_EMPTY;

public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    private Util() {

    }

    /**
     * This function of service is for validating expiration time of any request
     *
     * @param date String parameter
     * @return true if request is expired
     */

    public static boolean checkExpiryOfRequest(String date) {

        LOGGER.info("Check for expiry date of request :: " + API_HAS_STARTED_SUCCESSFULLY);

        boolean b = false;
        String s = String.valueOf(LocalDate.now());
        String currentYear = s.substring(0, 4);
        String currentMonth = s.substring(5, 7);
        String currentDate = s.substring(8);

        int dateCurrent = Integer.parseInt(currentDate);
        int monthCurrent = Integer.parseInt(currentMonth);
        int yearCurrent = Integer.parseInt(currentYear);

        String dataMonth = date.substring(5, 7);
        String dataDate = date.substring(8);
        String dataYear = date.substring(0, 4);

        int monthDatabase = Integer.parseInt(dataMonth);
        int dateDatabase = Integer.parseInt(dataDate);
        int yearDatabase = Integer.parseInt(dataYear);

        if (monthDatabase == monthCurrent && yearDatabase == yearCurrent) {
            int value = dateDatabase - dateCurrent;
            if (value < 0)
                b = true;
        }

        LOGGER.info("Expiry Status Received");
        return b;
    }

    public static void throwBadRequestException(String email) {
        if (email == null) {
            LOGGER.error(Constants.EMPTY_EMAIL);
            throw new BadRequestException(EMAIL_CANNOT_BE_EMPTY);
        }
    }

}
