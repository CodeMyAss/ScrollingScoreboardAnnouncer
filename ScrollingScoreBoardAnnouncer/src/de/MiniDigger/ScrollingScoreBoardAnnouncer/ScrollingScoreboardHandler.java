package de.MiniDigger.ScrollingScoreBoardAnnouncer;

import java.util.ArrayList;

public class ScrollingScoreboardHandler {

	private ScrollingScoreBoardConfig	                      config;

	public ArrayList<ScrollingScoreBoard>	boards	= new ArrayList<>();

	public void init(ScrollingScoreBoardConfig ssb_config) {
		config = ssb_config;
	}

	@SuppressWarnings("unchecked")
	public void loadAll() {
		boards = (ArrayList<ScrollingScoreBoard>) config.get("boards");
		if (boards == null) {
			boards = new ArrayList<>();
		}
	}

	public void saveAll() {
		config.set("boards", "");
		config.set("boards", boards);
	}

	public ScrollingScoreBoard get(String name) {
		for (ScrollingScoreBoard board : boards) {
			if (board.getName().equalsIgnoreCase(name)) {
				return board;
			}
		}
		return null;
	}

	public void create(String name, int title_length, int title_delay,
	        String title_msg, String title_color, String title_idle_msg,
	        Integer[] slot_lengths, Integer[] slot_delays, String[] slot_msgs,
	        String[] slot_colors, String[] slot_idle_msgs) {
		ScrollingScoreBoard ssc = new ScrollingScoreBoard(name, title_length,
		        title_delay, title_msg, title_color, title_idle_msg,
		        slot_lengths, slot_delays, slot_msgs, slot_colors,
		        slot_idle_msgs);
		boards.add(ssc);
	}

	public void delete(String name) {
		ScrollingScoreBoard board = null;
		for (ScrollingScoreBoard ssb : boards) {
			if (ssb.getName().equalsIgnoreCase("name")) {
				ssb.cancelAllTasks();
				board = ssb;
				break;
			}
		}
		if (board != null) {
			boards.remove(board);
		}
		saveAll();
	}

	public void announce(String name, String msg, int slot) {
		get(name).annonce(msg, slot);
	}
}
