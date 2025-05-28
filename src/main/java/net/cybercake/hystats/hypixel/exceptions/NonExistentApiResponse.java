package net.cybercake.hystats.hypixel.exceptions;

public class NonExistentApiResponse extends HyStatsError {

    public NonExistentApiResponse() {
        super(4, "API did not provide a response for this value.");
    }

}
