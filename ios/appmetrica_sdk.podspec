#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'appmetrica_sdk'
  s.version          = '0.0.1'
  s.summary          = 'A Flutter plugin for Yandex AppMetrica SDK.'
  s.description      = <<-DESC
  A Flutter plugin for Yandex AppMetrica SDK.
                       DESC
  s.homepage         = 'https://emallstudio.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'EM ALL iT Studio' => 'andrew@emallstudio.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'YandexMobileMetrica/Dynamic', '4.0.0'

  s.ios.deployment_target = '8.0'
end

