package imis.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import imis.client.authentication.AuthenticationConsts;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 22.6.13
 * Time: 14:24
 */
public class AccountUtil {

    public static String getUserPassword(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accountManager.getPassword(accounts[0]);
    }

    public static String getUserUsername(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accounts[0].name;
    }

    public static String getUserICP(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accountManager.getUserData(accounts[0], AuthenticationConsts.KEY_ICP);
    }

    public static Account getUserAccount(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accounts[0];
    }
}
