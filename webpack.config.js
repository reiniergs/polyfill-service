let webpack = require('webpack');
let merge = require('webpack-merge');
let HtmlWebpackPlugin = require('html-webpack-plugin');
let path = require('path');
let CleanWebpackPlugin = require('clean-webpack-plugin');

const TARGET = process.env.npm_lifecycle_event;
const BUILD_DIR = path.resolve(__dirname, 'src/main/webapp/assets/js');
const APP_DIR = path.resolve(__dirname, 'app/');

var commonConfig = {
    entry: {
        bootstrap: path.resolve(APP_DIR, 'index.js')
    },
    module : {
        loaders : [
            {
                test : /\.(js|jsx)?/,
                loader : 'babel-loader',
                exclude: [
                    path.resolve(__dirname, 'node_modules')
                ]

            },
            {
                test: /\.hbs$/,
                loader: "handlebars-loader"
            },
            {
                test: /\.(scss|css)$/,
                loaders: ["style-loader", "css-loader", "sass-loader"]
            }
        ]
    },
    plugins: [
        new CleanWebpackPlugin(['src/main/webapp/assets/js', 'src/main/webapp/landing.html'], {
            root: path.resolve(__dirname),
            verbose: true,
            dry: false
        }),
        new HtmlWebpackPlugin({
            filename: path.resolve(__dirname, 'src/main/webapp/index.html'),
            template: 'app/templates/webpack-app-template.hbs',
            title: 'Polyfill as a service',
            appMountId: 'app',
            inject: false
        })
    ]
};

const entryVendors = {
    vendor: ['react', 'react-dom', 'immutable', 'react-router']
};

if (TARGET === 'dev-web') {
    module.exports = merge(commonConfig, {
        entry: entryVendors,
        output: {
            path: BUILD_DIR,
            filename: '[name].js'
        },
        devtool: "#source-map",
        watch: true,
        plugins: [
            new webpack.optimize.CommonsChunkPlugin({
                names: ['vendor', 'manifest']
            })
        ]
    })
}

if (TARGET === 'build-web') {
    module.exports = merge(commonConfig, {
        entry: entryVendors,
        output: {
            path: BUILD_DIR,
            filename: '[name].[chunkhash].js'
        },
        plugins: [
            new webpack.optimize.CommonsChunkPlugin({
                names: ['vendor', 'manifest']
            }),

            new webpack.optimize.UglifyJsPlugin({
                compress: {
                    warnings: false
                }
            }),

            new webpack.DefinePlugin({
                'process.env.NODE_ENV': '"production"'
            })
        ]
    });
}

