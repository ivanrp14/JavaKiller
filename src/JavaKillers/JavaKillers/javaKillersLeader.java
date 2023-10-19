/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JavaKillers.JavaKillers;

import JavaKillers.javaKillersBase;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alex3
 */
public class javaKillersLeader extends javaKillersBase {
    HashMap<String, Point2D.Double> allyDroidsPositions = new HashMap<>();
    HashMap<String, Point2D.Double> enemyPositions = new HashMap<>();
    public void run() {

        while (true) {
            posicionLeader();
            execute();
        }
    }
    
    public void posicionLeader() {
        Point2D.Double posicion = new Point2D.Double(getX(), getY());
        try {
            broadcastMessage(posicion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (isTeammate(e.getName())) {
            return;
        }

        try {
            
            // Informar a los droides sobre el estado de la vida del líder
            broadcastMessage(getEnergy());
        } catch (IOException ex) {
            System.out.println("Error al enviar mensaje: " + ex.getMessage());
            Logger.getLogger(javaKillersLeader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Bloque de calculo de enemigos
        double distancia = e.getDistance();
        double bearing = e.getBearing();
        double angle = Math.toRadians(getHeading() + bearing);
        double enemigoX = getX() + distancia * Math.sin(angle);
        double enemigoY = getY() + distancia * Math.cos(angle);

        Point2D.Double enemyPosition = new Point2D.Double(enemigoX, enemigoY);
        enemyPositions.put(e.getName(), enemyPosition);
        findAndSendClosestEnemies(allyDroidsPositions, enemyPositions);
    }
    
    private void findAndSendClosestEnemies(Map<String, Point2D.Double> allyDroidsPositions, Map<String, Point2D.Double> enemyPositions) {
        for (Map.Entry<String, Point2D.Double> entry : allyDroidsPositions.entrySet()) {
            String closestEnemy = null;
            double minDistance = Double.MAX_VALUE;
            Point2D.Double closestEnemyPos = null;

            for (Map.Entry<String, Point2D.Double> enemyEntry : enemyPositions.entrySet()) {
                double distance = entry.getValue().distance(enemyEntry.getValue());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestEnemy = enemyEntry.getKey();
                    closestEnemyPos = enemyEntry.getValue();
                }
            }

            if (closestEnemy != null) {
                try {
                    sendMessage(entry.getKey(), closestEnemyPos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    
    public void onMessageReceived(MessageEvent e) {
    // Los droides envían su posición en un objeto Point2D.Double
    if (e.getMessage() instanceof Point2D.Double) {
        allyDroidsPositions.put(e.getSender(), (Point2D.Double)e.getMessage());
    }
}
}

