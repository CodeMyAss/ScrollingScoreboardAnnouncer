package de.MiniDigger.ScrollingScoreBoardAnnouncer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScrollingScoreBoardListener implements Listener{
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		ScrollingScoreBoardAnnouncer.handler.join(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e){
		ScrollingScoreBoardAnnouncer.handler.changedWorld(e.getPlayer());
	}

}
