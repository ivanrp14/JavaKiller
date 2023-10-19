package JavaKillers.PredatorTeam;
import JavaKillers.Enemy;
import JavaKillers.Vector2;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;
import robocode.util.Utils;
import static robocode.util.Utils.normalRelativeAngleDegrees;
/**
 *
 * @author ivanr
 */
public class PredatorTeam extends TeamRobot{

  
    enum State {
        HANDSHAKE, APROX, ORBIT, RAMMING
    }
    private long timeSearching = 0;
    private boolean sentidoHorario = true;
    private float speed;
    private Enemy target;
    private Enemy [] enemies; // Array para guardar la posición de los 5 enemigos
    private Vector2[] robotsPositions; // Array para guardar la posición de los 5 robots
    State state;
    private double aproxDistance;
    public void run(){
        
        aproxDistance = Math.hypot(getBattleFieldHeight(), getBattleFieldWidth()) * 0.20;
        state = State.HANDSHAKE;
        enemies = new Enemy[5];
        robotsPositions = new Vector2[5];
        timeSearching = 0;
        while(true){
            System.out.println(state);
            printInfo();
            if(target != null) System.out.println("target: "+target.getName());
            switch(state){
                case HANDSHAKE:
                    positionBroadcastMessage();
                    if(getEnemiesDetected() == 5 || (getEnemiesDetected() > 0 && getTime() > 120)){
                        target = selectTarget();
                        state = State.APROX;
                    }
                    else{
                        turnRight(90);
                        ahead(100);
                        turnRadarRight(360);
                    }
                    break;
                case APROX:
                  turnRight(normalRelativeAngleDegrees(angleTo(target.getPosition().x, target.getPosition().y) - getHeading()));
                    turnRadarRight(normalRelativeAngleDegrees(angleTo(target.getPosition().x, target.getPosition().y) - getRadarHeading()) );
                    turnGunRight(normalRelativeAngleDegrees(angleTo(target.getPosition().x, target.getPosition().y) - getGunHeading())) ;
                    while(euclideanDis( new Vector2(target.getPosition().x, target.getPosition().y),
                            new Vector2(getX(),getY())) > aproxDistance){
                        ahead(10);
                    }
                    state = State.ORBIT;
                    break;
                case ORBIT:
                    if(target.getEnergy() > 20)orbitEnemy(target, aproxDistance);
                    else state = State.RAMMING;
                    break;
                case RAMMING:
                   while(target.getEnergy() > 0.0){
                        turnRight(normalRelativeAngleDegrees(angleTo(target.getPosition().x, target.getPosition().y) - getHeading()));
                        turnRadarRight(normalRelativeAngleDegrees(angleTo(target.getPosition().x, target.getPosition().y) - getRadarHeading()) );
                        turnGunRight(normalRelativeAngleDegrees(angleTo(target.getPosition().x, target.getPosition().y) - getGunHeading())) ;
                        ahead(aproxDistance + 100);
                   }
                    for (int i = 0; i < 5; i++){
                        enemies[i] = null;
                        
                    }
                    state = State.HANDSHAKE;
            }    
            }
        }
    
     public void orbitEnemy(Enemy e,  double distance) {
        double deltaX = e.getPosition().x - getX();
        double deltaY = e.getPosition().y - getY();
        double currentDistance = Math.hypot(deltaX, deltaY); // Distancia actual al enemigo
        double angleToEnemy = angleTo(e.getPosition().x, e.getPosition().y);
        double angleOffset;
        if (sentidoHorario) angleOffset = 90; // Ángulo offset para mantener una órbita en sentido horario
        else angleOffset = -90;
        // Calcula el ángulo necesario para mantener la distancia deseada
        double desiredAngle = angleToEnemy + angleOffset;
        // Calcula la diferencia entre el ángulo actual y el deseado
        double turnAngle = normalRelativeAngleDegrees(desiredAngle - getHeading());
        // Gira hacia el ángulo deseado
        turnRight(turnAngle);
        // Ajusta la velocidad para mantener la distancia constante
        if (currentDistance > distance) {
            ahead(currentDistance - distance);
        } else if (currentDistance < distance) {
            back(distance - currentDistance);
        }
        
        turnRadarRight(normalRelativeAngleDegrees(angleToEnemy - getRadarHeading())) ;
        turnGunRight(normalRelativeAngleDegrees(angleToEnemy - getGunHeading()) );
        fire(1);
    
    }
     public void onScannedRobot(ScannedRobotEvent e) {
        if (!e.getName().contains("PredatorTeam")) {
            int enemyIndex;
             if (e.getName().contains("(")) {
                String s = String.valueOf(e.getName().charAt(e.getName().indexOf("(") + 1));
                enemyIndex = Integer.parseInt(s) - 1;
            } else {
                enemyIndex = 4;
            }
             Enemy enemy = new Enemy(
                         e.getName(),
                     getPositionFromBearing(e.getBearing(),e.getDistance()),
                     e.getEnergy()
                 );
             if (target != null && (target.getName().equals(e.getName()))){
                 target = enemy;
                 try {
                     this.broadcastMessage("target,"+enemy.getName()+","+enemy.getPosition().x+","+enemy.getPosition().y+","+enemy.getEnergy());
                } catch (IOException ex) {
                    Logger.getLogger(PredatorTeam.class.getName()).log(Level.SEVERE, null, ex);
                }
             }
             enemies[enemyIndex] = enemy;
            try {
                this.broadcastMessage("enemy,"+enemyIndex+","+enemy.getName()+","+enemy.getPosition().x+","+enemy.getPosition().y+","+enemy.getEnergy());
            } catch (IOException ex) {
                Logger.getLogger(PredatorTeam.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     private Vector2 getPositionFromBearing(double bearing, double distance){
         double enemyBearing = this.getHeading() + bearing;
        double enemyX = getX() + distance * Math.sin(Math.toRadians(enemyBearing));
        double enemyY = getY() + distance * Math.cos(Math.toRadians(enemyBearing));
        return new Vector2(enemyX,enemyY);
     }
     public void onMessageReceived(MessageEvent e) {
        String message = e.getMessage().toString();
        if (message.contains("enemy")) {
            String s[] = message.split(",");
            int index = Integer.parseInt(s[1]);
            enemies[index] = new Enemy(s[2], new Vector2(Double.parseDouble(s[3]), Double.parseDouble(s[4])), Double.parseDouble(s[5]));
        }
        if (message.contains("robotsPos")) {
            String s[] = message.split(",");
            int index = Integer.parseInt(s[1]);
            robotsPositions[index] = new Vector2(Double.parseDouble(s[2]), Double.parseDouble(s[3]));
        }
        if (message.contains("target")) {
            String s[] = message.split(",");
            target = new Enemy(s[1], new Vector2(Double.parseDouble(s[2]), Double.parseDouble(s[3])), Double.parseDouble(s[4]));
        }
        if (message.contains("cambioDeSentido")) {
            System.out.println("recibido Cambio de sentido");
            sentidoHorario = !sentidoHorario;
            
        }
    }
  private void positionBroadcastMessage() {
        // Enviar la posición actual a todos los miembros del equipo
        int Index;
        if (getName().contains("(")) {
            String s = String.valueOf(getName().charAt(getName().indexOf("(") + 1));
            Index = Integer.parseInt(s) - 1;
        } else {
            Index = 4;
        }
        robotsPositions[Index] = new Vector2(getX(), getY());
        try {
            this.broadcastMessage("robotsPos," + Index + "," + getX() + "," + getY());
        } catch (IOException ex) {
            Logger.getLogger(PredatorTeam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     private int getEnemiesDetected() {
        int e = 0;
        for (int i = 0; i < 5; i++) {
            if (enemies[i] != null) e++;
        }
        return e;
    }
     public double euclideanDis(Vector2 pos1, Vector2 pos2) {
        return Math.abs(Math.sqrt(Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2)));
    }
     private Enemy selectTarget() {
        Map<Integer, Double> maxDistances = new HashMap<>();
        int index = 0;
        for (int i = 0; i < 5; i++) {
            if (enemies[i] != null) {
                double distanceMAX = Double.MIN_VALUE;
                for (Vector2 ally : robotsPositions) {
                    double distance = euclideanDis(enemies[i].getPosition(), ally);
                    if (distance > distanceMAX) {
                        distanceMAX = distance;
                    }
                }
                maxDistances.put(i, distanceMAX);
            }
        }
        double minMaxDistance = Double.MAX_VALUE;
        
        for (Map.Entry<Integer, Double> entry : maxDistances.entrySet()) {
            if (entry.getValue() < minMaxDistance) {
                index = entry.getKey();
                minMaxDistance = entry.getValue();
            }
        }
        target = enemies[index];
         try {
                     this.broadcastMessage("target,"+target.getName()+","+target.getPosition().x+","+target.getPosition().y+","+target.getEnergy());
                } catch (IOException ex) {
                    Logger.getLogger(PredatorTeam.class.getName()).log(Level.SEVERE, null, ex);
                }
        return target;
    }
    public double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
    public double  angleTo(double x, double y){
         double angleToTarget = Math.atan2(x - getX(), y - getY());
        // Convertir el ángulo a grados
        double degreesToTarget = Math.toDegrees(angleToTarget);
        return degreesToTarget ;
    }
     private void printInfo() {
        
        for (int i = 0; i < 5; i++) {
            if (enemies[i] != null) {
                System.out.println("enemy " + enemies[i].getName() + " (" + enemies[i].getPosition().x + ", " + enemies[i].getPosition().y  + ")  ");
            }
        }
        for (int i = 0; i < 5; i++) {
            if (robotsPositions[i] != null) {
                System.out.println("robots " + i + " (" + robotsPositions[i].x + ", " + robotsPositions[i].y + ")  ");
            }
        }
    }
     public void onHitWall(HitWallEvent event) {
        if (state == State.HANDSHAKE) {
            turnRight(180);
        }
        if (state == State.ORBIT) {
            try {
                broadcastMessage("cambioDeSentido");
            } catch (IOException ex) {
                Logger.getLogger(PredatorTeam.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void onRobotDeath(RobotDeathEvent event){
         if(event.getName().equals(target.getName())){
             target.setEnergy(0);
         }
     }
     
}
