package com.foundry.drunkengranite.synonymity.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.foundry.drunkengranite.synonymity.Adapters.ChoiceAdapter;
import com.foundry.drunkengranite.synonymity.Async.Get.GetWords;
import com.foundry.drunkengranite.synonymity.Entities.Get.WordProblem;
import com.foundry.drunkengranite.synonymity.R;

import java.util.ArrayList;


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

public class GameFragment extends Fragment implements GetWords.onReturn, ChoiceAdapter.onClick
{
    private StateKeeper stateKeeper;
    private GridView options;
    private ArrayList<WordProblem> problems;
    private ChoiceAdapter adapter;
    private TextView currentWord;
    private boolean isLoading;
    private GetWords asyncActions;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//      initialize the game state manager (statekeeper), the empty adapter for the options grid,
//      and the current stack of problems. i.e. i want no null exceptions
//      note the statekeeper keeps track of the game, not the current word, ie the score and the timer

        this.stateKeeper = new StateKeeper(this);
        this.problems = new ArrayList<>();

//        callback of the adapter in the isCorrect method, where the onclick loads the next problems
        this.adapter = new ChoiceAdapter(getContext(), this);

//        we show the loading screen before dispatching the new asyn fetch
//        data is handled in @Method Callback
        showLoading();
        this.asyncActions = new GetWords(this);
        this.asyncActions.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View toReturn = inflater.inflate(R.layout.fragment_game, container, false);
//        initialize the gridrow, no null errors for the adapter
        this.options = (GridView) toReturn.findViewById(R.id.gridView);
        this.options.setAdapter(this.adapter);


        this.currentWord = (TextView) toReturn.findViewById(R.id.current_word);
        this.stateKeeper.setScoreContainer((TextView) toReturn.findViewById(R.id.score));
        this.stateKeeper.setTimerContainer((TextView) toReturn.findViewById(R.id.timer));

        return toReturn;
    }

    public void addProblems(ArrayList<WordProblem> wordProblems)
    {
        if (wordProblems.size() == 0)
        {
            System.out.println("error occured");
            return;
        }
        this.problems.addAll(wordProblems);
        WordProblem currProb = this.problems.remove(0);
        this.currentWord.setText(currProb.getCurrentWord());
        this.adapter.setNewProblem(currProb);
    }

    public void showLoading()
    {
        this.isLoading = true;
        if (getChildFragmentManager() != null)
        {
//          we use the child fragment manager because we want this to get removed if the back button
//          is pressed
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_parent, LoadingFragment.newInstance())
                    .addToBackStack("Loading")
                    .commitAllowingStateLoss();
        }
    }

    public void removeLoading()
    {
        System.out.println("here");
        this.isLoading = false;
        if (getChildFragmentManager() != null)
        {
            System.out.println("poping");
//            pops it out
            System.out.println(getChildFragmentManager().getBackStackEntryCount());
            getChildFragmentManager().popBackStack();

        }
    }


    @Override
    public void onCallback(ArrayList<WordProblem> input)
    {
//        we remove the async action, since it is done and ready to be garbage
        this.asyncActions = null;
//        remove the loading screen and insert the new problems into out problems queueu
        removeLoading();
        addProblems(input);

//        start the game
        stateKeeper.reset();
    }

    @Override
    public void isCorrect(boolean isCorrect)
    {
        if (isCorrect)
        {
//            pops a new one into the adapter, resets the state keeper
            if (this.problems.size() != 0)
            {
                WordProblem newProb = this.problems.remove(0);
                this.currentWord.setText(newProb.getCurrentWord());
                this.adapter.setNewProblem(newProb);
                stateKeeper.reset();
            }
//            needs more the queue, start it uo again
            else
            {
                stateKeeper.setPaused();
                showLoading();
                this.asyncActions = new GetWords(this);
                this.asyncActions.execute();
            }
        }
        else
        {
            kill();
        }

    }

    public void kill()
    {
        WordProblem missedProblem = adapter.getCurrentProblem();
        String selectedOption = adapter.getLastSelectedItem();

        this.adapter.clear();
        this.stateKeeper.kill();
        this.currentWord.setVisibility(View.GONE);
        System.out.println("should be replacing");
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_parent, FinishedGame.newInstance(missedProblem, selectedOption, stateKeeper.score))
                .commitAllowingStateLoss();
    }


    private class StateKeeper
    {
        private TextView scoreContainer;

        private TextView timerContainer;
        private int score;
        private boolean paused;
        private CountDownTimer countDownTimer;
        private GameFragment parent;

        //        initializes the data holders
        StateKeeper(GameFragment parent)
        {
            this.parent = parent;
            this.paused = false;
            this.score = 0;
        }


        //        this method increments the current score, resets the timer, increments the difficulty rating
//        and it
        void reset()
        {
            this.score += 10;
            this.scoreContainer.setText(String.valueOf(this.score));

            if (this.countDownTimer != null)
            {
                this.countDownTimer.cancel();
            }
            this.countDownTimer = null;
            this.countDownTimer = new CountDownTimer(10000, 10)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {
                    String time = String.valueOf(millisUntilFinished / 10);
                    if (time.length() < 2)
                    {
                        return;
                    }
                    timerContainer.setText(new StringBuilder(time).insert(time.length() - 2, ".").toString());
                }

                @Override
                public void onFinish()
                {
                    if (!paused)
                    {
                        parent.kill();
                    }
                }
            };
            this.countDownTimer.start();
        }


        void kill()
        {
//            I know this is an anti pattern, will fix later when integrating urban dictionary
//            todo cleaner code
            if (this.countDownTimer != null)
            {
                this.countDownTimer.cancel();
            }
            if (this.timerContainer != null)
            {
                this.timerContainer.setText("(✖╭╮✖)");
            }
        }

        void setScoreContainer(TextView scoreContainer)
        {
            this.scoreContainer = scoreContainer;
        }

        void setTimerContainer(TextView timerContainer)
        {
            this.timerContainer = timerContainer;
        }

        void setPaused()
        {
            this.paused = true;
        }


    }


    //junk methods go here

    public GameFragment()
    {
        // Required empty public constructor
    }

    public static GameFragment newInstance()
    {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    //  cleans up the delegates so no null errors or random async tasks for null managers to throw
    @Override
    public void onDetach()
    {
        super.onDetach();
        if (this.asyncActions != null)
        {
            this.asyncActions.delegate = null;
            this.asyncActions.cancel(true);
        }
        this.asyncActions = null;
        this.stateKeeper.kill();
    }


}
