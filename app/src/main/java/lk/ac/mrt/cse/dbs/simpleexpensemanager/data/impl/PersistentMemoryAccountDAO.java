package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.util.Log;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {


    private DatabaseHelper dh;

    public PersistentMemoryAccountDAO(DatabaseHelper dh){
        this.dh = dh;
    }

    @Override
    public List<String> getAccountNumbersList() {
        return  dh.getAccountNumbersList();

    }

    @Override
    public List<Account> getAccountsList() {
        return dh.getAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return dh.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dh.addAccount(account.getAccountNo(),account.getBankName(),account.getAccountHolderName(),account.getBalance());
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        dh.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        dh.updateBalance(accountNo,expenseType,amount);
    }
}
