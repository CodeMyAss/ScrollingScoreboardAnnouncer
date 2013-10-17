package de.MiniDigger.ScrollingScoreBoardAnnouncer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@SerializableAs("ScrollingScoreBoard")
public class ScrollingScoreBoard implements ConfigurationSerializable {

	public Scoreboard	board;
	private String	  name;
	private int	      title_length;
	private int	      title_delay;
	private int	      title_taskid;
	private int	      title_index;
	private String	  title_msg;
	private String	  title_color;
	private String	  title_idle_msg;
	private Integer[]	slot_lengths;
	private Integer[]	slot_delays;
	private Integer[]	slot_taskids;
	private Integer[]	slot_indexs;
	private String[]	slot_msgs;
	private String[]	slot_colors;
	private String[]	slot_idle_msgs;

	private Objective	ob;

	public ScrollingScoreBoard(String name, int title_length, int title_delay,
	        String title_msg, String title_color, String title_idle_msg,
	        Integer[] slot_lengths, Integer[] slot_delays, String[] slot_msgs,
	        String[] slot_colors, String[] slot_idle_msgs) {
		this.name = name;
		this.title_length = title_length;
		this.title_delay = title_delay;
		this.title_taskid = 0;
		this.title_index = 0;
		this.title_msg = title_msg;
		this.title_color = title_color;
		this.title_idle_msg = title_idle_msg;
		this.slot_lengths = slot_lengths;
		this.slot_delays = slot_delays;
		this.slot_taskids = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		this.slot_indexs = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		this.slot_msgs = slot_msgs;
		this.slot_colors = slot_colors;
		this.slot_idle_msgs = slot_idle_msgs;

		board = Bukkit.getScoreboardManager().getNewScoreboard();

	}

	public Objective init() {
		// Clearing
		if (board.getObjective(DisplaySlot.SIDEBAR) != null) {
			board.getObjective(DisplaySlot.SIDEBAR).unregister();
		}
		if (board.getObjective("msg_board") != null) {
			board.getObjective("msg_board").unregister();
		}
		final Objective obj = board.registerNewObjective("msg_board", "dummy");
		obj.setDisplayName(title_idle_msg);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		ob = obj;
		return obj;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("name", name);

		result.put("title_length", title_length);
		result.put("title_delay", title_delay);
		result.put("title_msg", title_msg);
		result.put("title_color", title_color);
		result.put("title_idle_msg", title_idle_msg);

		result.put("slot_lengths", slot_lengths);
		result.put("slot_delays", slot_delays);
		result.put("slot_msgs", slot_msgs);
		result.put("slot_colors", slot_colors);
		result.put("slot_idle_msgs", slot_idle_msgs);
		return result;
	}

	public static ScrollingScoreBoard deserialize(Map<String, Object> args) {
		String name = (String) args.get("name");

		int title_length = (int) args.get("title_length");
		int title_delay = (int) args.get("title_delay");
		String title_msg = (String) args.get("title_msg");
		String title_color = (String) args.get("title_color");
		String title_idle_msg = (String) args.get("title_idle_msg");

		Integer[] slot_lengths = (Integer[]) args.get("slot_lengths");
		Integer[] slot_delays = (Integer[]) args.get("slot_delays");
		String[] slot_msgs = (String[]) args.get("slot_msgs");
		String[] slot_colors = (String[]) args.get("slot_colors");
		String[] slot_idle_msgs = (String[]) args.get("slot_idle_msgs");

		return new ScrollingScoreBoard(name, title_length, title_delay,
		        title_msg, title_color, title_idle_msg, slot_lengths,
		        slot_delays, slot_msgs, slot_colors, slot_idle_msgs);
	}

	public void start(final Objective obj, final int slot) {

		// obj.getScore(Bukkit.getOfflinePlayer("TEST")).setScore(2);

		if (slot == -1) {
			title_taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(
			        ScrollingScoreBoardAnnouncer.getInstance(), new Runnable() {

				        @Override
				        public void run() {
					        String msg = title_color + next(title_msg, -1);
					        if (msg.length() <= title_color.length()) {
						        obj.setDisplayName(title_color + title_idle_msg);
						        cancelTask(-1);
					        } else {
						        title_index += title_length;
						        obj.setDisplayName(msg);
					        }
				        }
			        }, 20L * 3, title_delay);
		} else if (slot >= 0 && slot <= 9) {
			slot_taskids[slot] = Bukkit.getScheduler()
			        .scheduleSyncRepeatingTask(ScrollingScoreBoardAnnouncer.getInstance(),
			                new Runnable() {
				                private OfflinePlayer	player	= null;

				                @Override
				                public void run() {
					                if (player != null) {
						                board.resetScores(player);
					                }
					                String msg = "";
					                try {
						                msg = slot_colors[slot]
						                        + next(slot_msgs[slot], slot);
					                } catch (Exception e) {
						                // Main.debug("Ex");
					                }
					                if (msg.length() <= slot_colors[slot]
					                        .length()) {
						                player = Bukkit
						                        .getOfflinePlayer(slot_idle_msgs[slot]);
						                obj.getScore(player).setScore(slot + 1);
						                cancelTask(slot);
					                } else {
						                slot_indexs[slot] += slot_lengths[slot];
						                player = Bukkit.getOfflinePlayer(msg);
						                obj.getScore(player).setScore(slot + 1);
					                }
				                }
			                }, 20L * 3, slot_delays[slot]);
		} else {
			ScrollingScoreBoardAnnouncer.debug("Wrong slot number! (" + slot + ")");
		}
	}

	private String next(String s, int slot) {
		if (slot == -1) {
			if (s.length() <= 32) {
				return s;
			}
			return s.substring(
			        title_index,
			        Math.min(title_index + 32 - title_color.length(),
			                s.length() - title_color.length()));
		} else if (slot >= 0 && slot <= 9) {
			if (s.length() <= 16) {
				return s;
			}
			return s.substring(slot_indexs[slot], Math.min(slot_indexs[slot]
			        + 16 - slot_colors[slot].length(), s.length()
			        - slot_colors[slot].length()));
		} else {
			ScrollingScoreBoardAnnouncer.debug("Wrong slot number! (" + slot + ")");
			return "FAILED";
		}
	}

	public void annonce(String msg, int slot) {
		cancelTask(slot);
		if (slot == -1) {
			title_msg = msg;
			start(ob, -1);
		} else if (slot >= 0 && slot <= 9) {
			slot_msgs[slot] = msg;
			start(ob, slot);
		} else {
			ScrollingScoreBoardAnnouncer.debug("Wrong slot number! (" + slot + ")");
		}
	}

	public String getName() {
		return name;
	}

	public void cancelTask(int slot) {
		if (slot == -1) {
			Bukkit.getScheduler().cancelTask(title_taskid);
			title_taskid = 0;
			title_index = 0;
		} else if (slot >= 0 && slot <= 9) {
			Bukkit.getScheduler().cancelTask(slot_taskids[slot]);
			slot_taskids[slot] = 0;
			slot_indexs[slot] = 0;
		} else {
			ScrollingScoreBoardAnnouncer.debug("Wrong slot number! (" + slot + ")");
		}

	}

	public void cancelAllTasks() {
		cancelTask(-1);
		cancelTask(0);
		cancelTask(1);
		cancelTask(2);
		cancelTask(3);
		cancelTask(4);
		cancelTask(5);
		cancelTask(6);
		cancelTask(7);
		cancelTask(8);
		cancelTask(9);
	}

	public void startAll() {
		start(ob, -1);
		start(ob, 0);
		start(ob, 1);
		start(ob, 2);
		start(ob, 3);
		start(ob, 4);
		start(ob, 5);
		start(ob, 6);
		start(ob, 7);
		start(ob, 8);
		start(ob, 9);
	}
}
