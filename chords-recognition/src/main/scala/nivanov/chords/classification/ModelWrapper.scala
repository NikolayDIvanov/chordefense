package nivanov.chords.classification

import smile.classification.{Classifier, OneVersusRest, PlattScaling}

/**
 * Implemented due to lack of functionailiy for getting the OvR score in smile.
 */
class ModelWrapper[F](model: Classifier[F]):

  lazy val classifiers = {
    val field = model.getClass.getDeclaredField("classifiers")
    field.setAccessible(true)
    field.get(model).asInstanceOf[Array[Classifier[F]]]
  }

  lazy val platts = {
    val field = model.getClass.getDeclaredField("platts")
    field.setAccessible(true)
    field.get(model).asInstanceOf[Array[PlattScaling]]
  }

  def predictWithScore(features: F) =
    val predicted = model.predict(features)
    val score = model match
      case ovrSvm: OneVersusRest[F] => classifiers(predicted).score(features)
      case other => other.score(features)

    (predicted, score)

  def predictWithProbability(features: F) =
    val predicted = model.predict(features)
    val score = model match
      case ovrSvm: OneVersusRest[F] => platts(predicted).scale(classifiers(predicted).score(features))
      case other => other.score(features)

    (predicted, score)