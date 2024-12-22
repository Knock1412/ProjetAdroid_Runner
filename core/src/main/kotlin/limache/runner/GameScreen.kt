package limache.runner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

class GameScreen(private val game: MainRunner) : Screen {
    private val backgroundTexture = Texture("background.png")
    private val playerTexture = Texture("player.png")
    private val obstacleTexture = Texture("obstacle.png")
    private val coinTexture = Texture("coin.png") // Texture des pièces

    private val playerSprite = Sprite(playerTexture)
    private val obstacles = mutableListOf<Sprite>()
    private val coins = mutableListOf<Sprite>() // Liste des pièces

    private var playerVelocityY = 0f
    private val gravity = -20f
    private val jumpForce = 5f

    private val scrollSpeed = 5f
    private var backgroundOffsetX = 0f

    private var score = 0
    private val font = BitmapFont().apply { color = Color.WHITE }

    private var gameOver = false
    private var obstacleSpawnTimer = 0f
    private val obstacleSpawnInterval = 3f
    private var coinSpawnTimer = 0f
    private val coinSpawnInterval = 3f // Intervalle de génération des pièces (en ligne)
    private val viewport = FitViewport(12f, 7f)

    override fun show() {
        playerSprite.setSize(1f, 1f)
        playerSprite.setPosition(2f, viewport.worldHeight / 2f)
    }

    override fun render(delta: Float) {
        if (!gameOver) updateGame(delta)

        ScreenUtils.clear(Color.BLACK)
        viewport.apply()
        game.batch.projectionMatrix = viewport.camera.combined

        game.batch.begin()
        game.batch.draw(backgroundTexture, backgroundOffsetX, 0f, viewport.worldWidth, viewport.worldHeight)
        game.batch.draw(backgroundTexture, backgroundOffsetX + viewport.worldWidth, 0f, viewport.worldWidth, viewport.worldHeight)

        obstacles.forEach { it.draw(game.batch) }
        coins.forEach { it.draw(game.batch) } // Dessiner les pièces
        playerSprite.draw(game.batch)
        font.draw(game.batch, "Score: $score", 10f, viewport.worldHeight - 10f)
        game.batch.end()
    }

    private fun updateGame(delta: Float) {
        // Défilement de l'arrière-plan
        backgroundOffsetX -= scrollSpeed * delta
        if (backgroundOffsetX <= -viewport.worldWidth) {
            backgroundOffsetX = 0f
        }

        // Gestion des mouvements du joueur
        if (Gdx.input.isTouched) {
            playerVelocityY = jumpForce
        }
        playerVelocityY += gravity * delta
        playerSprite.translateY(playerVelocityY * delta)

        if (playerSprite.y < 0f) {
            playerSprite.y = 0f
            playerVelocityY = 0f
        } else if (playerSprite.y + playerSprite.height > viewport.worldHeight) {
            playerSprite.y = viewport.worldHeight - playerSprite.height
            playerVelocityY = 0f
        }

        // Génération des obstacles
        obstacleSpawnTimer += delta
        if (obstacleSpawnTimer >= obstacleSpawnInterval) {
            spawnObstacle()
            obstacleSpawnTimer = 0f
        }

        // Génération des pièces (en ligne)
        coinSpawnTimer += delta
        if (coinSpawnTimer >= coinSpawnInterval) {
            spawnCoinsLine()
            coinSpawnTimer = 0f
        }

        // Déplacement des obstacles et des pièces
        obstacles.forEach { it.translateX(-scrollSpeed * delta) }
        coins.forEach { it.translateX(-scrollSpeed * delta) }

        // Suppression des obstacles et pièces sortis de l'écran
        obstacles.removeAll { it.x + it.width < 0 }
        coins.removeAll { it.x + it.width < 0 }

        // Gestion des collisions avec les obstacles
        if (obstacles.any { it.boundingRectangle.overlaps(playerSprite.boundingRectangle) }) {
            game.screen = GameOverScreen(game) // Change d'écran en cas de collision
            return
        }

        // Gestion des collisions avec les pièces
        val coinIterator = coins.iterator()
        while (coinIterator.hasNext()) {
            val coin = coinIterator.next()
            if (coin.boundingRectangle.overlaps(playerSprite.boundingRectangle)) {
                coinIterator.remove() // Supprimer la pièce
                score += 10 // Augmenter le score
            }
        }

        // Augmentation du score avec le temps
        score += (10 * delta).toInt()
    }

    private fun spawnObstacle() {
        val obstacle = Sprite(obstacleTexture).apply {
            setSize(1f, 1f)
            setPosition(viewport.worldWidth, playerSprite.y)
        }
        obstacles.add(obstacle)
    }

    private fun spawnCoinsLine() {
        // Générer une hauteur aléatoire pour la ligne de pièces
        val coinY = MathUtils.random(1f, viewport.worldHeight - 1f)

        // Vérifier qu'il n'y a pas d'obstacles proches
        if (obstacles.none { it.y < coinY + 1f && it.y > coinY - 1f }) {
            // Générer une série de pièces alignées horizontalement
            for (i in 0..4) { // Une série de 5 pièces
                val coin = Sprite(coinTexture).apply {
                    setSize(0.5f, 0.5f)
                    setPosition(viewport.worldWidth + i * 0.7f, coinY) // Espacement de 0.7f entre les pièces
                }
                coins.add(coin)
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    override fun dispose() {
        backgroundTexture.dispose()
        playerTexture.dispose()
        obstacleTexture.dispose()
        coinTexture.dispose() // Libérer la texture des pièces
    }
}
