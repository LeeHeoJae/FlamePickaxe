package nexus;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.Config;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentFireAspect;
import cn.nukkit.item.enchantment.EnchantmentType;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.event.EventHandler;

import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.Arrays;

public class FlamePickaxe extends PluginBase implements Listener{
	ArrayList<Player> enchanting=new ArrayList<Player>();
	Config config;
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this,this);
		this.getDataFolder().mkdirs();
		this.saveResource("config.yml",false);
		this.config=new Config(this.getDataFolder()+"/config.yml",Config.YAML);
		this.config.save();
	}
	@Override
	public void onDisable(){
		this.config.save();
	}
	@EventHandler
	public void onTouch(PlayerInteractEvent ev){
		Item item=ev.getItem();
		int item_id=item.getId();
		Player player=ev.getPlayer();
		Inventory inventory=player.getInventory();
		Enchantment enchan=Enchantment.get(13);
 		if(item_id==Item.ENCHANTED_BOOK&&item.hasEnchantments()){
 			for(Enchantment enchantment:item.getEnchantments()){
 				if(enchantment.getId()!=13){
 					return;
 				}
 			}
			enchanting.add(player);
			player.sendPopup("§6발화 효과를 붙일 곡괭이로 다시 터치해주세요.");
			item.setCount(1);
			player.getInventory().removeItem(new Item[]{item});
			ev.setCancelled();
			return;
		}
		if(enchanting.contains(player)){
			if(!item.isPickaxe()){
				enchanting.remove(player);
				player.sendPopup("§4취소되었습니다.");
				Item book=new Item(Item.ENCHANTED_BOOK,0,1);
				book.addEnchantment(enchan);
				inventory.addItem(book);
				ev.setCancelled();
				return;
			}
			enchanting.remove(player);
			item.addEnchantment(enchan);
			player.getInventory().setItemInHand(item);
			player.sendPopup("§6발화 효과 추가 성공 !");
			ev.setCancelled();
			return;
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent ev){
		Item item=ev.getItem();
		int item_id=item.getId();
		Block block=ev.getBlock();
		int block_id=block.getId();
		Player player=ev.getPlayer();
		Inventory inventory=player.getInventory();
		Enchantment enchan=Enchantment.get(Enchantment.ID_FIRE_ASPECT);
		if(item.isPickaxe()&&item.hasEnchantments()){
			for(Enchantment enchantment:item.getEnchantments()){
 				if(enchantment.getId()!=13){
 					return;
 				}
 			}
 			if(player.isCreative()){
 				ev.setCancelled();
 				return;
 			}
			if(canFlame(block_id)){
				String $burned=this.config.getString(String.valueOf(block_id));//구워진 아이템
				
				int burned;
				int burned_damage;
				if(!isStringInt($burned)){//문자열일경우
					burned=Integer.parseInt($burned.split(":")[0]);
					burned_damage=Integer.parseInt($burned.split(":")[1]);
				}
				else{
					burned=Integer.parseInt($burned);
					burned_damage=0;
				}
				Item[] drops={new Item(burned,burned_damage,1)};
				ev.setDrops(drops);
				item.setDamage(item.getDamage()-1);
				player.getInventory().setItemInHand(item);
			}
		}
	}
	boolean canFlame(int block_id){
		if(!this.config.exists(String.valueOf(block_id))){
			return false;
		}
		return true;
	}
	boolean isStringInt(String s){
		try{
			Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException e){
			return false;
		}
	}
}