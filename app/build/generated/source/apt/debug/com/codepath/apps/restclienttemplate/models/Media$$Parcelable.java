
package com.codepath.apps.restclienttemplate.models;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.parceler.Generated;
import org.parceler.IdentityCollection;
import org.parceler.InjectionUtil;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;

@Generated("org.parceler.ParcelAnnotationProcessor")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class Media$$Parcelable
    implements Parcelable, ParcelWrapper<com.codepath.apps.restclienttemplate.models.Media>
{

    private com.codepath.apps.restclienttemplate.models.Media media$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static Creator<Media$$Parcelable>CREATOR = new Creator<Media$$Parcelable>() {


        @Override
        public Media$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new Media$$Parcelable(read(parcel$$2, new IdentityCollection()));
        }

        @Override
        public Media$$Parcelable[] newArray(int size) {
            return new Media$$Parcelable[size] ;
        }

    }
    ;

    public Media$$Parcelable(com.codepath.apps.restclienttemplate.models.Media media$$2) {
        media$$0 = media$$2;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(media$$0, parcel$$0, flags, new IdentityCollection());
    }

    public static void write(com.codepath.apps.restclienttemplate.models.Media media$$1, android.os.Parcel parcel$$1, int flags$$0, IdentityCollection identityMap$$0) {
        int identity$$0 = identityMap$$0 .getKey(media$$1);
        if (identity$$0 != -1) {
            parcel$$1 .writeInt(identity$$0);
        } else {
            parcel$$1 .writeInt(identityMap$$0 .put(media$$1));
            parcel$$1 .writeString(InjectionUtil.getField(String.class, com.codepath.apps.restclienttemplate.models.Media.class, media$$1, "mediaUrl"));
            parcel$$1 .writeInt((InjectionUtil.getField(boolean.class, com.codepath.apps.restclienttemplate.models.Media.class, media$$1, "hasMedia")? 1 : 0));
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.codepath.apps.restclienttemplate.models.Media getParcel() {
        return media$$0;
    }

    public static com.codepath.apps.restclienttemplate.models.Media read(android.os.Parcel parcel$$3, IdentityCollection identityMap$$1) {
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$1 .containsKey(identity$$1)) {
            if (identityMap$$1 .isReserved(identity$$1)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return identityMap$$1 .get(identity$$1);
        } else {
            com.codepath.apps.restclienttemplate.models.Media media$$4;
            int reservation$$0 = identityMap$$1 .reserve();
            media$$4 = new com.codepath.apps.restclienttemplate.models.Media();
            identityMap$$1 .put(reservation$$0, media$$4);
            InjectionUtil.setField(com.codepath.apps.restclienttemplate.models.Media.class, media$$4, "mediaUrl", parcel$$3 .readString());
            InjectionUtil.setField(com.codepath.apps.restclienttemplate.models.Media.class, media$$4, "hasMedia", (parcel$$3 .readInt() == 1));
            com.codepath.apps.restclienttemplate.models.Media media$$3 = media$$4;
            identityMap$$1 .put(identity$$1, media$$3);
            return media$$3;
        }
    }

}
