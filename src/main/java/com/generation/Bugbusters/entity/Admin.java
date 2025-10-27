package com.generation.Bugbusters.entity;

import java.util.List;

import lombok.Data;

@Data
public class Admin extends User implements IAdmin{

    private List<Guest> guests;
    private List<Guest> bannedGuests;

    public boolean ban(Long id){
        for(Guest g : guests){
            if(g.getId() == id){
                guests.remove(g);
                bannedGuests.add(g);
                return true;
            }
        }
        return false;
    }

    public boolean unBan(Long id){
        for(Guest g : bannedGuests){
            if(g.getId() == id){
                bannedGuests.remove(g);
                guests.add(g);
                return true;
            }
        }
        return false;

    }

}
