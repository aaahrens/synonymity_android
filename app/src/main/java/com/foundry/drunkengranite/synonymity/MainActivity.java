package com.foundry.drunkengranite.synonymity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.foundry.drunkengranite.synonymity.Fragments.MenuFragment;

/**
 * @author by drunkengranite
 * @date Created  on 5/20/17.
 * @description the android app for the game synonymity, where a user guesses synonyms
 * @class java 17.11
 * @class_description for use in the java class at the srjc 17.11 under sean kirkpatrick,
 * this is a java class at srjc
 * @maintainer drunkengranite
 * @license set ref MIT
 */

public class MainActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.parent, MenuFragment.newInstance())
                .addToBackStack("GAME")
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed()
    {
//       because of the initial transaction
        if (getSupportFragmentManager().getBackStackEntryCount() == 1)
        {
            finish();
            return;
        }
        super.onBackPressed();
    }

}
