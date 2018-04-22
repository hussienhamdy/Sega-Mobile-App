package com.example.ussien.sega.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ussien.sega.Model.ScoreBoard;
import com.example.ussien.sega.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by ussien on 29/09/2017.
 */

public class ScoreBoardAdapter extends TableDataAdapter<ScoreBoard>
{
    public ScoreBoardAdapter(Context context, ArrayList<ScoreBoard> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView)
    {
        final ScoreBoard scoreBoard = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderImage(scoreBoard.getImage(), parentView);
                break;
            case 1:
                renderedView = renderName(scoreBoard.getName());
                break;
            case 2:
                renderedView = renderProgress(scoreBoard.getProgress(),parentView);
                break;
            case 3:
                renderedView = renderPoints(scoreBoard.getPoints());
                break;
        }
        renderedView.setPadding(10,30,10,30);
        return renderedView;
    }

    private View renderPoints(int scoreBoardPoints) {
        final String points = Integer.toString(scoreBoardPoints);
        final TextView textView = new TextView(getContext());
        textView.setText(points);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(14);
        return textView;
    }

    private View renderName(String scoreBoardName) {
        final TextView textView = new TextView(getContext());
        textView.setText(scoreBoardName);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(12);
        return textView;
    }

    private View renderProgress(int scoreBoardProgress, ViewGroup parentView) {
        final View view = getLayoutInflater().inflate(R.layout.score_board_progress, parentView, false);
        ProgressBar progressBar = view.findViewById(R.id.score_progress);
        progressBar.setProgress(scoreBoardProgress);
        final TextView textView = view.findViewById(R.id.score_progress_txt);
        final String points = Integer.toString(scoreBoardProgress);
        textView.setText(points+"%");
        return view;
    }

    private View renderImage(String scoreBoardImage , ViewGroup parentView) {
        final View view = getLayoutInflater().inflate(R.layout.score_board_image, parentView, false);
        final SimpleDraweeView simpleDraweeView = view.findViewById(R.id.scoreBoard_image);
        Uri imageUri = Uri.parse(scoreBoardImage);
        simpleDraweeView.setImageURI(imageUri);
        return view;
    }

    public void update(int studentID, int progress, int points)
    {
        for(int i = 0 ; i < getData().size() ; i ++)
        {
            if(getData().get(i).getStudentID()==studentID)
            {
                getData().get(i).setPoints(points);
                getData().get(i).setProgress(progress);
                break;
            }
        }
    }
}
