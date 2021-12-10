package net.artux.columba;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Cache<T> {

    private final Class<T> typeParameterClass;
    private final SharedPreferences mSharedPreferences;
    private final Gson gson;

    public Cache(Class<T> typeParameterClass, Context context, Gson gson) {
        this.typeParameterClass = typeParameterClass;
        this.gson = gson;
        mSharedPreferences = context.getSharedPreferences(typeParameterClass.getName(), Context.MODE_PRIVATE);
    }

    public void put(String id, T object){
        mSharedPreferences.edit().putString(id, gson.toJson(object)).apply();
    }

    public T get(String id){
        if(!mSharedPreferences.contains(id))
            return null;
        return gson.fromJson(mSharedPreferences.getString(id, ""), typeParameterClass);
    }

    public void clear(){
        mSharedPreferences.edit().clear().apply();
    }
}