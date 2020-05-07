package comp575.helloworld;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Contact implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String image;
    public String name;
    public String email;
    public String mobile;

    public static final Creator<Contact> CREATOR = new Creator<Contact>(){
        @Override
        public Contact createFromParcel(Parcel in){
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size){
            return new Contact[size];
        }
    };

    public Contact(String image, String name, String email, String mobile){
        this.image = image;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public Contact(Parcel in){
        image = in.readString();
        name = in.readString();
        email = in.readString();
        mobile = in.readString();
    }

    public String toString(){
        return name;
    }
    public String imageToString(){
        return image;
    }
    public String nameToString(){
        return name;
    }
    public String emailToString(){
        return email;
    }
    public String mobileToString(){
        return mobile;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setMobile(String mobile){
        this.mobile = mobile;
    }
    public void setImage(String image){
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(mobile);
    }

}
