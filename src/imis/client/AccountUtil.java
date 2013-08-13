package imis.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/**
 * Utility methods for accessing info about user account.
 */
public class AccountUtil {

    public static final String ACCOUNT_TYPE = "imisoid";

    public static String getUserPassword(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accountManager.getPassword(accounts[0]);
    }

    public static String getUserICP(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accounts[0].name;
    }

    public static Account getUserAccount(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accounts[0];
    }
}
