package teamwork.com.teamwork.app;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mangesh on 26-04-2017.
 */
public class GeoCodingLocation {

    Geocoder geocoder;
    List<android.location.Address> addresses;

   public List<Address> getAddressDetails(String locationAddress, Context context) throws IOException {


       geocoder = new Geocoder(context, Locale.getDefault());
       addresses = geocoder.getFromLocationName(locationAddress, 1);//pass latLong here
       return  addresses;
   }
}
