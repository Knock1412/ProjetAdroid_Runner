package limache.runner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils

class MainMenuScreen(private val game: MainRunner) : Screen {
    private val menuTexture = Texture("MainMenu.png")

    override fun show() {}

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        game.batch.begin()
        game.batch.draw(menuTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        game.batch.end()

        // Commencer le jeu si l'écran est touché
        if (Gdx.input.isTouched) {
            game.screen = GameScreen(game)
        }
    }

    override fun resize(width: Int, height: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        menuTexture.dispose()
    }
}
