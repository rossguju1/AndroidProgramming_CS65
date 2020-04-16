# CodeIt2 README
Ross Guju

# Overview

I went through the clean apk which is given to us in the instructions and made it so that my ActionTab is the same functionally to the given clean apk.

# My Bug Changes

The following bugs are just the ones I remembed to take note. Some of the bugs I fixed may have included other bugs. Moreover the following list is not an exhaustive list of the bugs I found.

## Bug 1: 
``final EditText phoneView = (EditText) v.findViewById(R.id.checkBox1);``

To

``final EditText phoneView = (EditText) v.findViewById(R.id.editPhone);``

## Bug 2:
``final RatingBar ratingBar=(RatingBar) v.findViewById(R.id.editText1);``

To

``final RatingBar ratingBar=(RatingBar) v.findViewById(R.id.ratingBar1);``


## Bug 3
``View v= inflater.inflate(R.layout.chatfragment, container, false);``

To

``View v= inflater.inflate(R.layout.partyfragment, container, false);``

## Bug 4: 
``final Button partySaveBtn=v.findViewById(R.id.checkbutton1);``

To

``final Button partySaveBtn=v.findViewById(R.id.save_party_btn);``

## Bug 5: 
*fixed party set date*

*Note* I used my own implementation for selecting the date

## Bug 6:
*Added* ``String partyVenue=partyVenueView.getText().toString();``

## Bug 7
*fixed gender bug*

## Bug 8 
*Added* ``prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());``

## Bug 9 
*made it so you can input email in meet fragment*
