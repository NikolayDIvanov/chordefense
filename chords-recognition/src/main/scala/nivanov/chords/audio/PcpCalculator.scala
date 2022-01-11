package nivanov.chords.audio

object PcpCalculator:
  val defaultRefRate = 130.81f
  val logOf2 = Math.log(2)

  def compute(dft: Array[Float], sampleRate: Float, refRate: Float = defaultRefRate) =
    val pcp = Array.fill(12)(0d)

    // Calculate
    for p <- 0 until 12 do
      for l <- 0 until dft.size / 2 do
        if p == mapToPcpIndex(l, sampleRate, dft.size, refRate) then pcp(p) = pcp(p) + Math.pow(Math.abs(dft(l)), 2)

    // Normalize
    val pcpSum = pcp.sum
    pcp.map(_ / pcpSum)

  def mapToPcpIndex(dftIdx: Int, sampleRate: Float, dftLength: Int, refRate: Float) =
    if dftIdx == 0 then -1
    else (12 * log2((sampleRate * dftIdx) / (dftLength * refRate)) % 12).round.toInt

  def log2(x: Float) = Math.log(x) / logOf2 + 1e-10
