package com.manojbhadane.rxjavasearchdemo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.manojbhadane.rxjavasearchdemo.api.ApiClient;
import com.manojbhadane.rxjavasearchdemo.api.ApiService;
import com.manojbhadane.rxjavasearchdemo.databinding.ActivityMainBinding;
import com.manojbhadane.rxjavasearchdemo.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "dsd";

    private ApiService apiService;
    private ContactAdapter mAdapter;
    private ActivityMainBinding mDataBinding;
    private ArrayList<Contact> contactsList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private PublishSubject<String> publishSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mAdapter = new ContactAdapter(this, contactsList);
        mDataBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mDataBinding.recyclerview.setItemAnimator(new DefaultItemAnimator());
        mDataBinding.recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mDataBinding.recyclerview.setAdapter(mAdapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        DisposableObserver<List<Contact>> observer = getSearchObserver();
        disposable.add(publishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMapSingle(new Function<String, Single<List<Contact>>>() {
                    @Override
                    public Single<List<Contact>> apply(String s) throws Exception {
//                        mDataBinding.progress.setVisibility(View.VISIBLE);
                        return apiService.getContacts(null, s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribeWith(observer));


        // skipInitialValue() - skip for the first time when EditText empty
        disposable.add(RxTextView.textChangeEvents(mDataBinding.edtSearch)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContactsTextWatcher()));
        disposable.add(observer);

        // passing empty string fetches all the contacts
        publishSubject.onNext("");
//        mDataBinding.progress.setVisibility(View.VISIBLE);
    }

    private DisposableObserver<List<Contact>> getSearchObserver() {
        return new DisposableObserver<List<Contact>>() {
            @Override
            public void onNext(List<Contact> contacts) {
                contactsList.clear();
                contactsList.addAll(contacts);
                mAdapter.notifyDataSetChanged();
//                mDataBinding.progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private DisposableObserver<TextViewTextChangeEvent> searchContactsTextWatcher() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
//                mDataBinding.progress.setVisibility(View.VISIBLE);
                Log.d(TAG, "Search query: " + textViewTextChangeEvent.text());
                publishSubject.onNext(textViewTextChangeEvent.text().toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }

}
