package comp575.helloworld;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
* CodingWithMitch. (March 17, 2017). Android Beginner Tutorial #11 - ListView Filter and Search Bar
 *  Retrieved from https://www.youtube.com/watch?v=rdQN2U1JJvY
* ii--L--ii. (October 2, 2015). ImageView crop image disappear when change orientation.
*   Retrieved from https://stackoverflow.com/questions/32859801/imageview-crop-image-disappear-when-change-orientation
* Thakur, B. (July 16, 2013). Saving and Reading Bitmaps/Images from Internal memory in Android.
*   Retrieved from https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
* */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private ArrayAdapter<Contact> adapter;
    private ListView contactListView;
    private ContactRepository contactRepository;
    private final int SELECT_PICTURE = 100;
    private Uri selectedImageUri;
    private String ImageSave = "";
    private ImageView imageView;
    private String directory = "";
    private Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText searchBar = (EditText) findViewById(R.id.search);
        imageView = (ImageView) findViewById(R.id.contactImg);

        //set absolute path for directory for images
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        setDir(directory.getAbsolutePath());

        //ContactRepository
        contactRepository = new ContactRepository(this);

        //Setup Adapter
        contactListView = (ListView) findViewById(R.id.contactsListView);
        if(contactListView != null) {
            adapter = new ArrayAdapter<Contact>(this,
                    android.R.layout.simple_list_item_1, contacts);
            contactListView.setAdapter(adapter);
            contactListView.deferNotifyDataSetChanged();
            contactListView.setOnItemClickListener(this);

            contactRepository.getAllContacts().observe(this, new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> updatedContacts) {
                    //update the contacts list when the database changes
                    adapter.clear();
                    adapter.addAll(updatedContacts);
                }
            });

            //Filter and changes ListView depending on what is being searched
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    (MainActivity.this).adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }

        if(savedInstanceState != null){
            for(Parcelable contact : savedInstanceState.getParcelableArrayList(
                    "contacts")) {
                contacts.add((Contact) contact);
            }
        } else {
            //Add some Contacts
       /*
            contacts.add(new Contact("Joe Bloggs", "joe@bloggs.co.nz",
                    "021123456"));
            contacts.add(new Contact("Jane Doe", "jane@doe.co.nz",
                    "022123456"));
       */
        }
    }

    public void saveContact(View view) {
        //insert / update contact list/db
        EditText nameField = (EditText) findViewById(R.id.name);
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText phoneField = (EditText) findViewById(R.id.phone);
        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String phone = phoneField.getText().toString();
        String image = name.replace(' ', '_') + ".jpg";

        //create and save image to internal storage, and save image name in DB
        Bitmap bitmap = null;
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File mypath = new File(directory, image);

        FileOutputStream fos = null;

        //create bitmap Image
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Save directory to use for specifying path of image
        setDir(directory.getAbsolutePath());

        int end = 0;

        //update contacts info and image
        for(int i=0; i < contacts.size(); i++){
            name = nameField.getText().toString();
            email = emailField.getText().toString();
            phone = phoneField.getText().toString();
            if(contacts.get(i).name.equals(name)){
                contacts.get(i).setEmail(email);
                contacts.get(i).setMobile(phone);
                contacts.get(i).setImage(image);
                contactRepository.update(contacts.get(i));
            }else{
                end += 1;
            }
        }

        if(end == contacts.size()){
            contactRepository.insert(new Contact(image, name, email, phone));
        }

        Toast.makeText(this, "Contact saved",
                Toast.LENGTH_SHORT).show();
    }

    //Delete Contact
    public void delContact(View view){
        //delete contact from list/db
        EditText nameField = (EditText) findViewById(R.id.name);
        String name = nameField.getText().toString();

        for(int i=0; i < contacts.size(); i++){
            if(contacts.get(i).name.equals(name)){
                contactRepository.delete(contacts.get(i));
            }
        }

        Toast.makeText(this, "Contact deleted",
                Toast.LENGTH_SHORT).show();
    }

    public void uploadImg(View view){
        //allow user to open and upload an image from device
        //set Intent to start activity
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //display uploaded image into contact image placeholer, set Uri for later use to save it as bitmap image
        imageView = (ImageView) findViewById(R.id.contactImg);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();

                if (null != selectedImageUri) {
                    saveImage(selectedImageUri.toString());
                    imageView.setImageURI(selectedImageUri);
                    setSelectedImgURI(selectedImageUri);

                }

            }
        }
    }

    //getters and setters for global variables
    public void setSelectedImgURI(Uri select){
        this.selectedImageUri = select;
    }

    public void setDir(String path){
        this.directory = path;
    }

    public String getDir(){
        return directory;
    }

    public void saveImage(String imageUp){
        this.ImageSave = imageUp;
    }

    public void setBitMap(Bitmap b){
        this.photo = b;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = (Contact) parent.getAdapter().getItem(position);
        Toast.makeText(parent.getContext(), "Clicked " + contact,
                Toast.LENGTH_SHORT).show();
        //Load text to text field
        ImageView imageView = (ImageView) findViewById(R.id.contactImg);
        EditText nameField = (EditText) findViewById(R.id.name);
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText phoneField = (EditText) findViewById(R.id.phone);
        nameField.setText(contact.nameToString());
        emailField.setText(contact.emailToString());
        phoneField.setText(contact.mobileToString());

        //load image to image placeholder
        try{
            File f = new File(getDir(), contact.imageToString());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            setBitMap(b);
            imageView.setImageBitmap(b);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    //Keep image when changing orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Bitmap image = savedInstanceState.getParcelable("BitmapImage");
        this.photo = image;
        imageView.setImageBitmap(photo);
        super.onRestoreInstanceState(savedInstanceState);
    }

    //setting text and image into a save state
    @Override
    public void onSaveInstanceState(Bundle savedState){
        savedState.putParcelableArrayList("contacts", contacts);
        savedState.putParcelable("BitmapImage", photo);
        super.onSaveInstanceState(savedState);
    }
}
