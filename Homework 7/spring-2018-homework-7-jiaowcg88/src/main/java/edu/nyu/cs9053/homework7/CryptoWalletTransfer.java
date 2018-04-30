package edu.nyu.cs9053.homework7;

public class CryptoWalletTransfer {

    public <T extends Cryptocurrency> void transfer(CryptoWallet<? extends T> fromCryptoWallet, CryptoWallet<? super T> intoCryptoWallet) {
        if (fromCryptoWallet == null || intoCryptoWallet == null) {
            throw new IllegalArgumentException("Input should not be null");
        }
        for (int i = 0; i < fromCryptoWallet.getSize(); i++) {
            intoCryptoWallet.add(fromCryptoWallet.get(i));
        }
    }

}
