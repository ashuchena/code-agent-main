const path = require('path')

module.exports = {
  publicPath: '/',
  outputDir: path.resolve(__dirname, '../../campus-agent/src/main/resources/static'),
  devServer: {
    port: 8080
  },
  productionSourceMap: false,
  configureWebpack: {
    cache: false
  }
}
