package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class controller implements Initializable{

	@FXML
	private Pane pane;
	@FXML
	private Label myLabel,myVolLabel;
	@FXML
	private Button playButton,pauseButton, resetButton,prevButton,nextButton;
	@FXML
	private ComboBox<String> speedButton;
	@FXML
	private Slider myVolumeSlider;
	@FXML
	private ProgressBar songProgressBar;

	private Media media;
	private MediaPlayer mediaPlayer;

	private File directory;
	private File[] files;

	private ArrayList<File> songs;

	private int songNumber;
	private int [] speeds = {25,50,75,100,125,150,175,200};
	private Timer timer;
	private TimerTask task;
	private boolean running;
	private int currVolume;



	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		songs = new ArrayList<File>();

		directory = new File("music");

		files = directory.listFiles();

		if(files != null)
		{
			for(File i : files)
			{
				songs.add(i);


			}

		}
		media = new Media(songs.get(songNumber).toURI().toASCIIString());
		mediaPlayer = new MediaPlayer(media);

		myLabel.setText(songs.get(songNumber).getName());

		for (int j = 0 ; j < speeds.length ; j++)
		{
			speedButton.getItems().add(Integer.toString(speeds[j]) + "%");
		}

		speedButton.setOnAction(this::speedMedia);

		myVolumeSlider.valueProperty().addListener(new ChangeListener<Number>()
		{

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) 
			{
				mediaPlayer.setVolume(myVolumeSlider.getValue() * 0.01);
				currVolume = (int)myVolumeSlider.getValue();
				myVolLabel.setText(Integer.toString(currVolume) + "%");

			}

		}
				);

		songProgressBar.setStyle("-fx-accent : #3ae42e;");



	}
	public void playMedia()
	{
		beginTimer();
		speedMedia(null);
		mediaPlayer.setVolume(myVolumeSlider.getValue() * 0.01);

		mediaPlayer.play();
	}
	public void pauseMedia()
	{
		cancelTimer();
		mediaPlayer.pause();
	}
	public void prevMedia()
	{
		if(songNumber > 0)
		{
			songNumber--;

			mediaPlayer.stop();

			if(running)
				cancelTimer();

			media = new Media(songs.get(songNumber).toURI().toASCIIString());
			mediaPlayer = new MediaPlayer(media);

			myLabel.setText(songs.get(songNumber).getName());

		}
		else
		{
			songNumber = songs.size()-1 ;

			mediaPlayer.stop();

			if(running)
				cancelTimer();

			media = new Media(songs.get(songNumber).toURI().toASCIIString());
			mediaPlayer = new MediaPlayer(media);

			myLabel.setText(songs.get(songNumber).getName());
		}

	}
	public void nextMedia()
	{
		if(songNumber < songs.size() - 1)
		{
			songNumber++;

			mediaPlayer.stop();

			if(running)
				cancelTimer();

			media = new Media(songs.get(songNumber).toURI().toASCIIString());
			mediaPlayer = new MediaPlayer(media);

			myLabel.setText(songs.get(songNumber).getName());

		}
		else
		{
			songNumber = 0;

			mediaPlayer.stop();

			if(running)
				cancelTimer();

			media = new Media(songs.get(songNumber).toURI().toASCIIString());
			mediaPlayer = new MediaPlayer(media);

			myLabel.setText(songs.get(songNumber).getName());
		}
	}
	public void resetMedia()
	{
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0.0));
		mediaPlayer.pause();
	}
	public void speedMedia(ActionEvent event)
	{
		if(speedButton.getValue() == null)
			mediaPlayer.setRate(1);
		else {
			//mediaPlayer.setRate(Integer.parseInt(speedButton.getValue()) * 0.01);
			mediaPlayer.setRate(Integer.parseInt(speedButton.getValue().substring(0, speedButton.getValue().length() -1)) * 0.01);
		}
	}
	public void beginTimer()
	{
		timer = new Timer();
		task = new TimerTask()
		{
			public void run()
			{
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				songProgressBar.setProgress(current/end);

				if(current/end == 1)
				{
					cancelTimer();
				}
			}


		};
		timer.scheduleAtFixedRate(task, 0, 100);

	}
	public void cancelTimer()
	{
		running = false;
		timer.cancel();

	}




}
