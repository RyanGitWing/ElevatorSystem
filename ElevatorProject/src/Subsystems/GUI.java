/**
 * 
 */
package Subsystems;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;
import java.awt.Font;
/**
 * @author Dominique Giguere Samson
 *To do: Direction arrow, faults
 */
public class GUI {
    JFrame f;
    JTable jt;
    int PORTOFFSET = 5000;
    String data[][] ={  {"22","","","",""},    
                         {"21","","","",""},    
                         {"20","","","",""},
                         {"19","","","",""},
                         {"18","","","",""},
                         {"17","","","",""},
                         {"16","","","",""},
                         {"15","","","",""},
                         {"14","","","",""},
                         {"13","","","",""},
                         {"12","","","",""},
                         {"11","","","",""},
                         {"10","","","",""},
                         {"9","","","",""},
                         {"8","","","",""},
                         {"7","","","",""},
                         {"6","","","",""},
                         {"5","","","",""},
                         {"4","","","",""},
                         {"3","","","",""},
                         {"2","","","",""},
                         {"1","","","",""}};
    
    /**
     * 
     */
    public GUI() {
        f=new JFrame();
       
        String column[]={"Floor","Elevator 1","Elevator 2","Elevator 3","Elevator 4"};         
        jt=new JTable(data,column);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0 ; i < 5; i++) {
        	jt.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        jt.setFont(new Font("Trebuchet", Font.BOLD, 30));
        jt.getTableHeader().setFont( new Font( "Trebuchet" , Font.BOLD, 30 ));
        jt.setRowHeight(35);
        jt.setBounds(30,40,600,900);
        
        JScrollPane sp=new JScrollPane(jt);
        f.add(sp);          
        f.setSize(1200,900);  
        f.setVisible(true);
    }
    
    
    public void updateGUI(ArrayList<int[]> ElevatorDisplayStats) {
        for (int[] elevator: ElevatorDisplayStats) {
            int id = elevator[0] - PORTOFFSET;
            int cur = elevator[1];
            int dir = elevator[2];
            int moving = elevator[3];
            int fault = elevator[4];
            for (int i = 1; i < 23; i++) {
            	if (fault > 0) {//Elevator broken
            		data[i-1][id+1] = "ERROR";
            	} else {
	                if (23-cur == i) {//Elevator is at this floor 
	            		if (moving == 1) { //Elevator is moving
		                	if (dir == 1) {
		                		//Elevator going up
		                        data[i-1][id+1] = "Up";
		                	} else {
		                		//Elevator going down
		                		data[i-1][id+1] = "Down";
		                	}
	            		} else {//Elevator not moving
	            			data[i-1][id+1] = "Stopped";
	            		}
	                } else {
	                    data[i-1][id+1] = "";
	                }
            	}
            	jt.repaint();
            }
        }
    }
}