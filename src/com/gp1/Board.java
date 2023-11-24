package com.gp1;

import javax.swing.*;

import com.gp1.sprite.Alien;
import com.gp1.sprite.Player;
import com.gp1.sprite.Shot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Random;



class Node<T> {
    T data;
    Node<T> next;

    public Node(T data) {
        this.data = data;
    }
}

class UserDefinedLinkedList<T> {
    private Node<T> head;

    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
    }
    public void remove(T data) {
    if (head == null) {
        return;
    }

    if (head.data.equals(data)) {
        head = head.next;
        return;
    }

    Node<T> current = head;
    while (current.next != null && !current.next.data.equals(data)) {
        current = current.next;
    }

    if (current.next != null) {
        current.next = current.next.next;
    }
}

    public Iterable<T> getItems() {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private Node<T> current = head;

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }

                    @Override
                    public T next() {
                        T data = current.data;
                        current = current.next;
                        return data;
                    }
                };
            }
        };
    }
}


public class Board extends JPanel {

    private Dimension d;
    private UserDefinedLinkedList<Alien> aliens;
    private Player player;
    private Shot[] shots;
//
   private Image explosionImage;
    private int score = 0;
    private int[] heap;
    private int size;
    private int capacity;
    private String username;
    private JButton restartButton;
    private TAdapter tAdapter = new TAdapter();
    private boolean inGame = true;

    private String message = "Game Over";

    private Timer timer;

    private Random random = new Random();

    public Board(String username) {
        this.username = username;
        initBoard();
        restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> tAdapter.restartGame());
        restartButton.setVisible(false);
        add(restartButton);
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
    
        // Set a background image
    
        timer = new Timer(Commons.DELAY, new GameCycle());
        timer.start();
        initHeap();
        gameInit();
    }
    
  

    private void gameInit() {
        System.out.println("Welcome, " + username + "!");
        aliens = new UserDefinedLinkedList<>();
        player = new Player();
        shots = new Shot[Commons.BOARD_WIDTH]; // Assuming the maximum number of shots is Commons.MAX_SHOTS
        generateRandomAlien();  // Initial alien generation
        
    }
    
    private void initHeap() {
        capacity = 10;
        size = 0;
        heap = new int[capacity];
    }

    private void generateRandomAlien() {
        var alien = new Alien(random.nextInt(651) + 50, 0);
        aliens.add(alien);
    }

    private void drawAliens(Graphics g, int offsetY) {
        for (Alien alien : aliens.getItems()) {
            if (alien.isVisible() && !alien.isDying() && alien.getImage() != null) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY() + offsetY, this);
            }
            if (alien.isDying()) {
                alien.die();
            }
        }
    }

    private void drawPlayer(Graphics g, int offsetY) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY() + offsetY, this);
        }
        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g, int offsetY) {
        for (Shot shot : shots) {
            if (shot != null && shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY() + offsetY, this);
            }
        }
    }

    

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.red);

        int offsetY = 20; // Adjust this value to lower or raise the drawing position

        if (inGame) {
            g.drawLine(0, Commons.GROUND, Commons.BOARD_WIDTH, Commons.GROUND);
            drawAliens(g, offsetY);
            drawPlayer(g, offsetY);
            drawShot(g, offsetY);
            // drawBombing(g, offsetY);
            g.setColor(Color.white);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Highest Scores:", Commons.BOARD_WIDTH - 150, 20);
            g.drawString(String.valueOf(heap[0]), Commons.BOARD_WIDTH - 150, 40);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, Commons.BOARD_WIDTH / 2 - 230, Commons.BOARD_WIDTH - 100, 50); // Adjusted Y-coordinate
        g.setColor(Color.white);
        g.drawRect(50, Commons.BOARD_WIDTH / 2 - 230, Commons.BOARD_WIDTH - 100, 50); // Adjusted Y-coordinate
    
        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);
    
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (Commons.BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2, Commons.BOARD_WIDTH / 2 - 200); // Adjusted Y-coordinate
        restartButton.setBounds(Commons.BOARD_WIDTH / 2 - 50, Commons.BOARD_HEIGHT / 2 - 60, 100, 40); // Adjusted Y-coordinate
        restartButton.setVisible(true);  // Make the restart button visible when the game is over
        g.setColor(Color.white);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Highest Scores:", Commons.BOARD_WIDTH - 150, 20);
        insert(score);
        g.drawString(String.valueOf(heap[0]), Commons.BOARD_WIDTH - 150, 40);
        score = 0;
        add(restartButton);
    }
    

    private void update() {
        updatePlayer();
        updateShots();
        detectCollisions();
        generateAliens();
        moveAliens();
        checkGameOver();
    }
    
    private void updatePlayer() {
        player.act();
    }
    
    private void updateShots() {
        for (Shot shot : shots) {
            if (shot != null && shot.isVisible()) {
                moveShot(shot);
            }
        }
    }
    
    private void moveShot(Shot shot) {
        int y = shot.getY() - 4;
        if (y < 0) {
            shot.die();
        } else {
            shot.setY(y);
        }
    }
    
    private void detectCollisions() {
        for (Shot shot : shots) {
            if (shot != null && shot.isVisible()) {
                for (Alien alien : aliens.getItems()) {
                    if (alien.isVisible() && shot.isVisible() && isCollision(shot, alien)) {
                        handleCollision(shot, alien);
                    }
                }
            }
        }
    }
    
    private boolean isCollision(Shot shot, Alien alien) {
        int alienX = alien.getX();
        int alienY = alien.getY();
        int shotX = shot.getX();
        int shotY = shot.getY();
    
        return shotX >= alienX && shotX <= (alienX + Commons.ALIEN_WIDTH) &&
               shotY >= alienY && shotY <= (alienY + Commons.ALIEN_HEIGHT);
    }
    
    private void handleCollision(Shot shot, Alien alien) {
        explosionImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/blast.png"));
        alien.setImage(explosionImage);
        alien.setDying(true);
        repaint();
        shot.die();
        score += 20;

        aliens.remove(alien);
        
    }
    
    private void generateAliens() {
        if (random.nextInt(100) < 5) {
            generateRandomAlien();
        }
    }
    
    private void moveAliens() {
        for (Alien alien : aliens.getItems()) {
            int x = alien.getX();
            int y = alien.getY();
            alien.setX(x);
            alien.setY(y + Commons.GO_DOWN);
        }
    }
    
    private void checkGameOver() {
        for (Alien alien : aliens.getItems()) {
            if (alien.getY() > Commons.GROUND - Commons.ALIEN_HEIGHT) {
                inGame = false;
                message = "Game Over";
            }
        }
       
    }
    
    private void doGameCycle() {
        update();
        repaint();
    }
    
    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                if (inGame) {
                    for (int i = 0; i < shots.length; i++) {
                        if (shots[i] == null || !shots[i].isVisible()) {
                            shots[i] = new Shot(x, y);
                            break;
                        }
                    }
                }
            }else if (key == KeyEvent.VK_R) {
                restartGame();
            }
        }
       
        public void restartGame() {
            inGame = true;
            score = 0;
            // message = "Game Over";
            gameInit();
        
            remove(restartButton);
            timer.restart();
            repaint();
        }
    }
     // MaxHeap implementation
     int parent(int i) {
        return (i - 1) / 2;
    }

    int leftChild(int i) {
        return 2 * i + 1;
    }

    int rightChild(int i) {
        return 2 * i + 2;
    }

    void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    void resize() {
        int oldCapacity = capacity;
        capacity *= 2;
        int[] newArray = new int[capacity];
        System.arraycopy(heap, 0, newArray, 0, oldCapacity);
        heap = newArray;
    }

    public void insert(int value) {
        if (size >= capacity) {
            resize();
        }
        size++;
        int i = size - 1;
        heap[i] = value;
        while (i != 0 && heap[parent(i)] < heap[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public void printHeap() {
        for (int i = 0; i < size; i++) {
            System.out.print(heap[i] + " ");
        }
        System.out.println();
    }
}
