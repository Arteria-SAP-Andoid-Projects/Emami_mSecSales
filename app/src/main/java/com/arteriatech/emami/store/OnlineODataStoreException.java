package com.arteriatech.emami.store;

public class OnlineODataStoreException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public OnlineODataStoreException(String errorMessage) {
        super(errorMessage);
    }

    public OnlineODataStoreException(Throwable throwable) {
        super(throwable);
    }

}
