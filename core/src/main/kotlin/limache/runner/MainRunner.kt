package limache.runner

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MainRunner : Game() {
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()
        setScreen(MainMenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        super.dispose()
    }
}


