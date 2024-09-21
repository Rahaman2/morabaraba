// package com.morabaraba;

// import com.badlogic.gdx.Game;
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// import com.badlogic.gdx.ApplicationAdapter;
// import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.graphics.GL20;
// import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// public class MainGame extends Game {
//     ShapeRenderer shapeRenderer;

//     @Override
//     public void create() {
//         shapeRenderer = new ShapeRenderer();
//     }

//     @Override
//     public void render() {
//         Gdx.gl.glClearColor(1, 1, 1, 1);
//         Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//         shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//         drawBoard();
//         shapeRenderer.end();
//     }

//     private void drawBoard() {
//         float width = Gdx.graphics.getWidth();
//         float height = Gdx.graphics.getHeight();

//         // Draw the grid for the board
//         for (int i = 0; i <= 8; i++) {
//             shapeRenderer.line(i * width / 8, 0, i * width / 8, height); // Vertical lines
//             shapeRenderer.line(0, i * height / 8, width, i * height / 8); // Horizontal lines
//         }

//         // Example pieces
//         shapeRenderer.circle(width / 4, height / 4, 20);
//         shapeRenderer.circle(3 * width / 4, 3 * height / 4, 20);
//     }

//     @Override
//     public void dispose() {
//         shapeRenderer.dispose();
//     }

//     public static void main(String[] args) {
//         // Create the game application
//         // LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//         // config.title = "Morabaraba Game";
//         // config.width = 800;
//         // config.height = 600;
//         // new LwjglApplication(new MainGame(), config);
//     }
// }
