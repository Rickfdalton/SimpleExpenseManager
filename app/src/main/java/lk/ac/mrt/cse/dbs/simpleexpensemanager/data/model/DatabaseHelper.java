package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME="ExpenseManager.db";
    public static int DATABASE_VERSION=1;

    public static final String TABLE_LOG="log";
    public static final String LOG_ID="log_id";
    public static final String LOG_DATE="date";
    public static final String ACC_NO="acc_No";
    public static final String TYPE="type";
    public static final String AMOUNT="amount";

    public static final String TABLE_ACC="acc";
    public static final String ACC_NUM="acc_No";
    public static final String BANK="bank";
    public static final String ACC_HOLDER="acc_holder";
    public static final String INITIAL_BALANCE="initial_balance";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String query2 = "CREATE TABLE "+TABLE_ACC +" ("+ ACC_NUM +" TEXT PRIMARY KEY,"+ BANK +" TEXT, "
                +ACC_HOLDER+" TEXT,"+
                INITIAL_BALANCE+" NUMERIC);";
        db.execSQL(query2);

        String query1 ="CREATE TABLE "+TABLE_LOG+" ("+ LOG_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +LOG_DATE +" TEXT, " +
                ACC_NO +" TEXT, "+
                TYPE + " TEXT,"+
                AMOUNT + " NUMERIC, FOREIGN KEY ("+ACC_NO+") REFERENCES "+TABLE_ACC+"("+ACC_NUM+"));";

        db.execSQL(query1);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LOG);
    }

    public void addAccount(String acc_no, String bank, String acc_holder, double initial_bal,int t){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues cv =new ContentValues();

        cv.put(ACC_NUM,acc_no);
        cv.put(BANK,bank);
        cv.put(ACC_HOLDER,acc_holder);
        cv.put(INITIAL_BALANCE,initial_bal);

        long result = db.insert(TABLE_ACC,null,cv);
        if (t==0) {
            if (result == -1) {
                Toast.makeText(context, "failed to add account", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();

            }
        }

    }

    public void removeAccount(String acc_no){
        SQLiteDatabase db =this.getWritableDatabase();
        db.delete(TABLE_ACC,ACC_NUM +"="+acc_no,null);
    }

    public Account getAccount(String acc){
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor c =db.rawQuery("SELECT "+ACC_NUM+","+BANK+","+ACC_HOLDER+","+INITIAL_BALANCE+" FROM "+TABLE_ACC+" WHERE "+ACC_NUM+"= '"+acc+"';" ,null);

       if( c.moveToNext()){
           return ( new Account(c.getString(c.getColumnIndexOrThrow(ACC_NUM)),c.getString(c.getColumnIndexOrThrow(BANK)),c.getString(c.getColumnIndexOrThrow(ACC_HOLDER)),Double.parseDouble(c.getString(c.getColumnIndexOrThrow(INITIAL_BALANCE)))));

       }
       else{
           return null;
       }
    }

    public List<String> getAccountNumbersList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =db.rawQuery("SELECT "+ACC_NUM+","+BANK+","+ACC_HOLDER+","+INITIAL_BALANCE+" FROM "+TABLE_ACC+" ;",null);
        List<String> accountNumbers = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                accountNumbers.add(c.getString(0));

            } while (c.moveToNext());
            // moving our cursor to next.
        }
        return accountNumbers;
    }

    public List<Account> getAccounts(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =db.rawQuery("SELECT "+ACC_NUM+","+BANK+","+ACC_HOLDER+","+INITIAL_BALANCE+" FROM "+TABLE_ACC+" ;",null);
        List<Account> accounts = new ArrayList<Account>();
        if (c.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                accounts.add(new Account(c.getString(0),c.getString(1),c.getString(2),c.getDouble(3)));
            } while (c.moveToNext());
            // moving our cursor to next.
        }
        return accounts;

    }



    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase db = this.getReadableDatabase();
        if (expenseType==ExpenseType.EXPENSE){
            double amnt= this.getAccount(accountNo).getBalance() - amount;
            if (amnt>0){

                Cursor c=db.rawQuery("UPDATE "+TABLE_ACC+" SET "+ INITIAL_BALANCE+" = '"+amnt+"' WHERE "+ACC_NUM+" = '"+ accountNo +"';",null);
              c.moveToFirst();
                c.close();

            }
            else{
                Toast.makeText(context, "not enough money", Toast.LENGTH_SHORT).show();

            }

        }else{
            double amnt= this.getAccount(accountNo).getBalance() + amount;
            String string_amnt=new Double(amnt).toString();
            Cursor c=db.rawQuery("UPDATE "+TABLE_ACC+" SET "+ INITIAL_BALANCE+" = "+string_amnt+" WHERE "+ACC_NUM+" = '"+ accountNo +"';",null);
            c.moveToFirst();
            c.close();
        }


    }

    public void addLog(Date date, String acc_No, ExpenseType type, Double amount){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues cv =new ContentValues();



        cv.put(LOG_DATE, date.toString());
        cv.put(ACC_NO,acc_No);
        cv.put(TYPE, String.valueOf(type));
        cv.put(AMOUNT,amount);

        long result = db.insert(TABLE_LOG,null,cv);
        if(result==-1){
            Toast.makeText(context, "failed to add log", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();

        }


    }



    public List<Transaction> getTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =db.rawQuery("SELECT "+LOG_DATE+","+ACC_NUM+","+TYPE+","+AMOUNT+" FROM "+TABLE_LOG+" ;",null);
        List<Transaction> transactions = new ArrayList<Transaction>();
        if (c.moveToFirst()) {
            do {
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                Date date = null;
                try {
                    date = format.parse(this.parseTodaysDate(c.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // on below line we are adding the data from cursor to our array list.
                transactions.add(new Transaction(date,c.getString(1),ExpenseType.valueOf(c.getString(2)),c.getDouble(3)));
            } while (c.moveToNext());
            // moving our cursor to next.
        }
        return transactions;

    }

    public static String parseTodaysDate(String time) {



        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";

        String outputPattern = "dd-MM-yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

            Log.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
