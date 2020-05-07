package comp575.helloworld;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class ContactRepository {
    private ContactDao contactDao;
    private LiveData<List<Contact>> allContacts;

    ContactRepository(Context context) {
        ContactRoomDatabase db = ContactRoomDatabase.getDatabase(context);
        contactDao = db.contactDao();
        allContacts = contactDao.getAllContacts();
    }

    public void insert(Contact contact){
        new InsertAsyncTask().execute(contact);
    }

    private class InsertAsyncTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(final Contact... params) {
            for(Contact contact : params){
                contactDao.insert(contact);
            }
            return null;
        }
    }

    public void update(Contact contact){
        new UpdateAsyncTask().execute(contact);
    }

    private class UpdateAsyncTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(final Contact... params) {
            for(Contact contact : params) {
                contactDao.update(contact);
            }
            return null;
        }
    }

    public void delete (Contact contact){
        new DeleteAsyncTask().execute(contact);
    }

    private class DeleteAsyncTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(final Contact... params) {
            for(Contact contact : params) {
                contactDao.delete(contact);
            }
            return null;
        }
    }

    public LiveData<List<Contact>> getAllContacts() {
        return allContacts;
    }

}
