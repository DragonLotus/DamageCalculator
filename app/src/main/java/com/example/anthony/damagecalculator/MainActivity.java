package com.example.anthony.damagecalculator;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.anthony.damagecalculator.Fragments.EnemyTargetFragment;
import com.example.anthony.damagecalculator.Fragments.MainFragment;
import com.example.anthony.damagecalculator.Fragments.MonsterPageFragment;
import com.example.anthony.damagecalculator.Fragments.TeamListFragment;
import com.integralblue.httpresponsecache.HttpResponseCache;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
{

   /**
    * The {@link android.support.v4.view.PagerAdapter} that will provide
    * fragments for each of the sections. We use a
    * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
    * loaded fragment in memory. If this becomes too memory intensive, it
    * may be best to switch to a
    * {@link android.support.v4.app.FragmentStatePagerAdapter}.
    */
   SectionsPagerAdapter mSectionsPagerAdapter;

   /**
    * The {@link android.support.v4.view.ViewPager} that will host the section contents.
    */
   ViewPager mViewPager;

   //private Spinner spinner;
   private static final String[] orbChoices = {"3", "4"};

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

      // Set up the ViewPager with the sections adapter.
      mViewPager = (ViewPager) findViewById(R.id.pager);
      mViewPager.setAdapter(mSectionsPagerAdapter);

      // Get the Default External Cache Directory
      File httpCacheDir = getExternalCacheDir();

      // Cache Size of 5MB
      long httpCacheSize = 5 * 1024 * 1024;

      try
      {
         // Install the custom Cache Implementation
//            HttpResponseCache.install(httpCacheDir, httpCacheSize);


      } catch (Exception e)
      {
         e.printStackTrace();
      }

   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings)
      {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }


   /**
    * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
    * one of the sections/tabs/pages.
    */
   public class SectionsPagerAdapter extends FragmentPagerAdapter
   {

      public SectionsPagerAdapter(FragmentManager fm)
      {
         super(fm);
      }

      @Override
      public Fragment getItem(int position)
      {
         // getItem is called to instantiate the fragment for the given page.
         // Return a PlaceholderFragment (defined as a static inner class below).
         if (position == 0)
         {
            return MainFragment.newInstance(position + 1);
         }
         if (position == 2)
         {
            return MonsterPageFragment.newInstance("thomas", "fix this");
         }
         if (position == 1)
         {
            return EnemyTargetFragment.newInstance("thomas", "where are you");
         }
         if(position == 3)
         {
            return TeamListFragment.newInstance("thomas ??", "sup");
         }

         return PlaceholderFragment.newInstance(position + 1);
      }

      @Override
      public int getCount()
      {
         // Show 3 total pages.
         return 4;
      }

      @Override
      public CharSequence getPageTitle(int position)
      {
         Locale l = Locale.getDefault();
         switch (position)
         {
            case 0:
               return getString(R.string.title_section1).toUpperCase(l);
            case 1:
               return getString(R.string.title_section2).toUpperCase(l);
            case 2:
               return getString(R.string.title_section3).toUpperCase(l);
            case 3:
               return "Section 4".toUpperCase(l);
         }
         return null;
      }
   }

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment
   {
      /**
       * The fragment argument representing the section number for this
       * fragment.
       */
      private static final String ARG_SECTION_NUMBER = "section_number";

      /**
       * Returns a new instance of this fragment for the given section
       * number.
       */
      public static PlaceholderFragment newInstance(int sectionNumber)
      {
         PlaceholderFragment fragment = new PlaceholderFragment();
         Bundle args = new Bundle();
         args.putInt(ARG_SECTION_NUMBER, sectionNumber);
         fragment.setArguments(args);
         return fragment;
      }

      public PlaceholderFragment()
      {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState)
      {
         View rootView = inflater.inflate(R.layout.fragment_main, container, false);
         return rootView;
      }
   }

}
