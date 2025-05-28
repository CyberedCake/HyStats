package net.cybercake.hystats.hypixel.exceptions;

public class UnusualApiResponse extends HyStatsError {

    public UnusualApiResponse(Throwable throwable) {
        super(3, throwable);
    }

}
