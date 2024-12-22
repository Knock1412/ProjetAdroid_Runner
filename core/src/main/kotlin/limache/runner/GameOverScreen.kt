package limache.runner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils

class GameOverScreen(private val game: MainRunner) : Screen {
    private val gameOverTexture = Texture("Game_Over_Menu.png")

    override fun show() {}

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        game.batch.begin()
        game.batch.draw(gameOverTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        game.batch.end()

        if (Gdx.input.isTouched) {
            game.screen = MainMenuScreen(game)
        }
        if (gameOverTexture.textureObjectHandle == 0) {
            println("Erreur : l'image Game_Over_Menu.png n'est pas chargée correctement.")
        }
    }

    override fun resize(width: Int, height: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        gameOverTexture.dispose()
    }
}

