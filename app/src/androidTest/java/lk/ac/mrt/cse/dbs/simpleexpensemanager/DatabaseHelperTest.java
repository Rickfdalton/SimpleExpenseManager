package lk.ac.mrt.cse.dbs.simpleexpensemanager;
import static android.support.test.InstrumentationRegistry.getTargetContext;

import junit.framework.TestCase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DatabaseHelper;

public class DatabaseHelperTest extends TestCase {
    private DatabaseHelper db;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        db = new DatabaseHelper(getTargetContext());

    }

    public void testAddAccount() {
        db.addAccount("android_test_4","android_test_4","android_test_4",100,1);
        Account acc = db.getAccount("android_test_4");
        assertTrue(acc!=null);
    }

    public void testGetAccount() {
        Account acc = db.getAccount("android_test_4");
        assertTrue(acc!=null);

    }
    public void testRemoveAccount() {
        db.removeAccount("'android_test_4'");
        Account acc = db.getAccount("android_test_4");
        assertTrue(acc==null);

    }


}