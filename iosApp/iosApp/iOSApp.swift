import UIKit
import ComposeApp

private let darkThemeStatusBarNotification = Notification.Name("CoppyStatusBarDarkTheme")
private let lightThemeStatusBarNotification = Notification.Name("CoppyStatusBarLightTheme")

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        return true
    }

    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        let configuration = UISceneConfiguration(
            name: "Default Configuration",
            sessionRole: connectingSceneSession.role
        )
        configuration.delegateClass = SceneDelegate.self
        return configuration
    }
}

final class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?
    private var themedRootViewController: ThemedRootViewController?

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = scene as? UIWindowScene else { return }

        let window = UIWindow(windowScene: windowScene)
        let rootViewController = ThemedRootViewController(
            contentViewController: MainViewControllerKt.MainViewController()
        )
        themedRootViewController = rootViewController
        window.rootViewController = rootViewController
        window.makeKeyAndVisible()
        self.window = window
    }
}

final class ThemedRootViewController: UIViewController {
    private let contentViewController: UIViewController
    private var isDarkTheme = false

    init(contentViewController: UIViewController) {
        self.contentViewController = contentViewController
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        addChild(contentViewController)
        view.addSubview(contentViewController.view)
        contentViewController.view.frame = view.bounds
        contentViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        contentViewController.didMove(toParent: self)

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(applyDarkThemeStatusBar),
            name: darkThemeStatusBarNotification,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(applyLightThemeStatusBar),
            name: lightThemeStatusBarNotification,
            object: nil
        )
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        isDarkTheme ? .lightContent : .darkContent
    }

    @objc private func applyDarkThemeStatusBar() {
        guard !isDarkTheme else { return }
        isDarkTheme = true
        setNeedsStatusBarAppearanceUpdate()
    }

    @objc private func applyLightThemeStatusBar() {
        guard isDarkTheme else { return }
        isDarkTheme = false
        setNeedsStatusBarAppearanceUpdate()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}
