package com.mclark4386.testrx;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static Observer<User> _getObserver(){
        return new Observer<User>() {
            @Override
            public void onCompleted() {
                Log.d("TEXTRX","onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("TEXTRX","onError:"+e.getLocalizedMessage());

            }

            @Override
            public void onNext(User s) {
                Log.d("TEXTRX","onNext:"+s.getId());
            }
        };
    }

    public class MapResults{

    }

    public interface testAPI{
        @GET("/users/{username}")
        User getUserJson(@Path("username")String username);
        @GET("/users/{username}")
        Observable<User> getUser(@Path("username")String username);
    }

    public interface testMap{
        @GET("/maps/api/geocode/json")
        List<MapResults> getResultsForAddress(@Query("address")String address,@Query("sensor")String sensor);
    }

    public static testAPI _api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestAdapter ra = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://api.github.com")
                .build();

        if(_api == null) {
            _api = ra.create(testAPI.class);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public static final String TAG = "TESTRX_Frag";
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        public TextView label;
        private Subscription timerSub;

        private EditText email;
        private EditText password;
        private EditText phone;
        private Button btn;

        private Observable<OnTextChangeEvent> emailObs;
        private Observable<OnTextChangeEvent> passObs;
        private Observable<OnTextChangeEvent> phoneObs;

        private EditText ghname;
        private TextView ghresults;

        private Observable<OnTextChangeEvent> ghnameObs;

        private boolean validateEmailEvent(OnTextChangeEvent email){
            return email.text() != null
                    && !email.text().toString().isEmpty()
                    && email.text().toString().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        }

        private boolean validatePassEvent(OnTextChangeEvent pass){
            return pass.text() != null
                    && !pass.text().toString().isEmpty()
                    && pass.text().length() > 8;
        }

        private boolean validatePhoneEvent(OnTextChangeEvent phone){
            return phone.text() != null
                    && !phone.text().toString().isEmpty()
                    && phone.text().toString().matches("(?:[0-9\\-_])*");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            label = (TextView) rootView.findViewById(R.id.section_label);

            btn = (Button) rootView.findViewById(R.id.button);
            btn.setEnabled(false);
            btn.setBackgroundColor(Color.WHITE);

            email = (EditText) rootView.findViewById(R.id.email);
            password = (EditText) rootView.findViewById(R.id.password);
            phone = (EditText) rootView.findViewById(R.id.phone);

            emailObs = WidgetObservable.text(email);
            passObs = WidgetObservable.text(password);
            phoneObs = WidgetObservable.text(phone);

            ghname = (EditText)rootView.findViewById(R.id.ghname);
            ghnameObs = WidgetObservable.text(ghname);
            ghresults = (TextView)rootView.findViewById(R.id.ghResults);
            ghresults.setEnabled(false);

            //base examples
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    final Handler handler = new Handler(); // bound to this thread

                    Observable.just("mclark4386")
                            .map(new Func1<String, User>() {
                                @Override
                                public User call(String username) {
                                    return _api.getUserJson(username);
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(_getObserver());
                }
            },"network-thread-1").start();

            //base but using retrofit properly and adding some extra steps (see that onComplete is only called the once)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    final Handler handler = new Handler(); // bound to this thread
                    _api.getUser("mclark4386")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .repeat(4)
                            .subscribe(_getObserver());
                }
            },"network-thread-2").start();

            //timer exam
            int Start_Delay = 0;
            int Polling_interval = 5;
            final int timer_count = 10;

            timerSub = Observable.timer(Start_Delay, Polling_interval, TimeUnit.SECONDS)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Long tickCount) {
                            //happens on "tick"
                            Log.d(TAG, "onNext 'tick':"+tickCount.toString());

                            if(tickCount > timer_count){
                                timerSub.unsubscribe();
                            }
                        }
                    });

            //Playing with debounce
            //This will "buffer" events so you don't spam every character

            int debounce_timeout = 400;
            ghnameObs.debounce(debounce_timeout, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<OnTextChangeEvent>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(OnTextChangeEvent onTextChangeEvent) {
                            _api.getUser(onTextChangeEvent.text().toString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<User>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(User user) {
                                            ghresults.setText(user.getName());
                                        }
                                    });
                        }
                    });

            //widget observers
            emailObs.subscribe(new Observer<OnTextChangeEvent>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(OnTextChangeEvent onTextChangeEvent) {
                    if(!validateEmailEvent(onTextChangeEvent))
                        email.setError("Invalid Email!");
                }
            });
            passObs.subscribe(new Observer<OnTextChangeEvent>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(OnTextChangeEvent onTextChangeEvent) {
                    if(!validatePassEvent(onTextChangeEvent))
                        password.setError("Password must be at least 8 characters!");
                }
            });
            phoneObs.subscribe(new Observer<OnTextChangeEvent>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(OnTextChangeEvent onTextChangeEvent) {
                    if(!validatePhoneEvent(onTextChangeEvent))
                        phone.setError("Must exist and contain only numbers, '_'s or '-'s.");
                }
            });

            //playing with combinelatest
            //NOTE: onNext isn't fired until ALL obvervables fire at least once

            Observable.combineLatest(emailObs, passObs, phoneObs, new Func3<OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, Boolean>() {
                @Override
                public Boolean call(OnTextChangeEvent onEmailChangeEvent, OnTextChangeEvent onPassChangeEvent, OnTextChangeEvent onPhoneChangeEvent) {
                    return validateEmailEvent(onEmailChangeEvent) && validatePassEvent(onPassChangeEvent) && validatePhoneEvent(onPhoneChangeEvent);
                }
            }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Boolean valid) {
                            if(valid){
                                btn.setEnabled(true);
                                btn.setBackgroundColor(Color.BLUE);
                            }else{
                                btn.setEnabled(false);
                                btn.setBackgroundColor(Color.WHITE);
                            }
                        }
                    });

            //ZIP ALL THE THINGS!!
            //NOTE: this waits for at least one from each and then every other event(or something like that) NOT WORKING... 
//            Observable.zip(emailObs, passObs, phoneObs, new Func3<OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, Boolean>() {
//                @Override
//                public Boolean call(OnTextChangeEvent onEmailChangeEvent, OnTextChangeEvent onPassChangeEvent, OnTextChangeEvent onPhoneChangeEvent) {
//                    Boolean emailValid = onEmailChangeEvent.text() != null
//                            && !onEmailChangeEvent.text().toString().isEmpty()
//                            && onEmailChangeEvent.text().toString().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
//
//                    if(!emailValid)
//                        email.setError("Invalid Email!");
//
//                    Boolean passValid = onPassChangeEvent.text() != null
//                            && !onPassChangeEvent.text().toString().isEmpty()
//                            && onPassChangeEvent.text().length() > 8;
//
//                    if(!passValid)
//                        password.setError("Password must be at least 8 characters!");
//
//                    Boolean phoneValid = onPhoneChangeEvent.text() != null
//                            && !onPhoneChangeEvent.text().toString().isEmpty()
//                            && onPhoneChangeEvent.text().toString().matches("(?:[0-9\\-_])*");
//
//                    if(!phoneValid)
//                        phone.setError("Must exist and contain only numbers, '_'s or '-'s.");
//
//                    return emailValid && passValid && phoneValid;
//                }
//            }).subscribeOn(AndroidSchedulers.mainThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<Boolean>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onNext(Boolean valid) {
//                            if(valid){
//                                btn.setEnabled(true);
//                                btn.setBackgroundColor(Color.BLUE);
//                            }else{
//                                btn.setEnabled(false);
//                                btn.setBackgroundColor(Color.WHITE);
//                            }
//                        }
//                    });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onDetach() {
            super.onDetach();
            timerSub.unsubscribe();
        }
    }

}
